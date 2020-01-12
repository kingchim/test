package com.changgou.file.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.file.pojo.FastDFSFile;
import com.changgou.file.pojo.FastDFSClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {
    @PostMapping("/upload")
    public Result uploadFile(MultipartFile file) {
        try {
            if (file == null) {
                throw new RuntimeException("文件不存在");
            }
            String originalFilename = file.getOriginalFilename();
            if (StringUtils.isEmpty(originalFilename)) {
                throw new RuntimeException("文件不存在");
            }
            // 获取文件的扩展名称  abc.jpg   jpg
            // originalFilename.lastIndexOf(".")为获取文件名的最后一个“.”位置的索引值
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            //获取文件内容
            byte[] content = file.getBytes();
            //创建文件上传的封装实体类
            FastDFSFile fastDFSFile = new FastDFSFile(originalFilename, content, extName);
            //基于工具类进行文件上传,并接受返回参数  String[]
            String[] upload = FastDFSClient.upload(fastDFSFile);
            //封装返回结果
            String s = FastDFSClient.getTrackerUrl() + upload[0] + "/" + upload[1];
            return new Result(true, StatusCode.OK, "文件上传成功", s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false, StatusCode.ERROR, "文件上传失败");
    }

}
