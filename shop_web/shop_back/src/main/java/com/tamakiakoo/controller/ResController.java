package com.tamakiakoo.controller;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tamakiakoo.utils.BaseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/res")
public class ResController {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;


    @RequestMapping("/uploadImg")
    @ResponseBody
    public BaseResult uploadImg(MultipartFile file){

        //System.out.println("uploadImg被调用了~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        String filename= file.getOriginalFilename();

        long size = file.getSize();
        int index = filename.lastIndexOf(".");
        String houzui = filename.substring(index + 1);


        try {
            //图片的输入流,图片的大小,图片的后缀(jpg/png....),元数据
            StorePath storePath = fastFileStorageClient.uploadImageAndCrtThumbImage(file.getInputStream(), size, houzui, null);
            return BaseResult.success("上传成功", storePath.getFullPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return BaseResult.fail();
    }
/*
@RequestMapping("/uploadImg")
    @ResponseBody
    public String uploadImg(MultipartFile file){

        String filename= file.getOriginalFilename();
        System.out.println("filename:"+filename);

        System.out.println("文件大小:"+file.getSize());

        System.out.println("name:"+file.getName());

        int index = filename.lastIndexOf(".");

        String houzui = filename.substring(index);

        String path = ResController.class.getResource("/").getPath()+"static/file/";

        String filepath = path+UUID.randomUUID().toString()+houzui;
        System.out.println(filepath);

        try (
                InputStream inputStream = file.getInputStream();
                OutputStream outputStream = new FileOutputStream(filepath);
        ) {
            IOUtils.copy(inputStream, outputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return filepath;
    }
*/

}
