package com.project.community.controller;

import com.project.community.annotation.LoginRequired;
import com.project.community.entity.User;
import com.project.community.service.FollowService;
import com.project.community.service.LikeService;
import com.project.community.service.UserService;
import com.project.community.util.CommunityConstant;
import com.project.community.util.CommunityUtil;
import com.project.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author makwingchi
 * @Description
 * @create 2020-04-16 22:42
 */
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @GetMapping("/setting")
    @LoginRequired
    public String getSettingPage() {
        return "/site/setting";
    }

    @PostMapping("/upload")
    @LoginRequired
    public String uploadHead(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "you haven't selected an image");
            return "/site/setting";
        }
        // check suffix
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "wrong format");
            return "/site/setting";
        }

        // generate random file name
        fileName = CommunityUtil.generateUUID() + suffix;
        // where is the file saved?
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // save the header image file
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException("file upload failure");
        }

        // update user header url
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // path in server
        fileName = uploadPath + "/" + fileName;
        // get suffix
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // send response
        response.setContentType("/image" + suffix);
        try (
                OutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(fileName);
                ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("cannot read header" + e.getMessage());
        }
    }

    @PostMapping("/updatePassword")
    @LoginRequired
    public String updatePassword(String oldPassword, String newPassword, String confirmPassword, Model model) {
        // null check
        if (oldPassword == null) {
            model.addAttribute("opError", "You haven't typed in the original password");
            return "/site/setting";
        }
        if (newPassword == null) {
            model.addAttribute("npError", "You haven't typed in a new password");
            return "/site/setting";
        }
        if (confirmPassword == null) {
            model.addAttribute("cpError", "You haven't typed in the confirmed password");
            return "/site/setting";
        }

        // get the corresponding encrypted password and perform comparison
        String oldPasswordPlusSalt = oldPassword + hostHolder.getUser().getSalt();
        String oldEncrypted = CommunityUtil.md5(oldPasswordPlusSalt);
        if (!oldEncrypted.equals(hostHolder.getUser().getPassword())) {
            model.addAttribute("wpError", "Wrong password");
            return "/site/setting";
        }
        // length check
        if (newPassword.length() < 8) {
            model.addAttribute("lengthError", "Length of new password should be LONGER than 8");
            return "/site/setting";
        }
        // whether two passwords match or not
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("unmatchError", "Confirmed password should be the SAME as new password");
            return "/site/setting";
        }
        // update user password
        String newPasswordPlusSalt = newPassword + hostHolder.getUser().getSalt();
        String encrypted = CommunityUtil.md5(newPasswordPlusSalt);
        userService.updatePassword(hostHolder.getUser().getId(), encrypted);

        return "redirect:/index";
    }

    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("user does not exist");
        }
        // user
        model.addAttribute("user", user);
        // likes
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);
        // followees
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // followers
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // followed?
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }

}

