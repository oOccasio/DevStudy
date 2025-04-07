package chuchuchi.chuchuchi.global.file.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class FileServiceTest {

    @Autowired
    FileService fileService;

    private MockMultipartFile getMockUploadFile() throws IOException {
        String content = "fake image content";
        return new MockMultipartFile(
                "file",
                "file.jpg",
                "image/jpeg",
                content.getBytes());
    }

    @Test
    public void fileSaveSuccess() throws Exception{
        //given, when
        String filePath = fileService.save(getMockUploadFile());

        //then
        File file = new File(filePath);
        assertThat(file.exists()).isTrue();

        //finally
        file.delete();
    }

    @Test
    public void fileDeleteSuccess() throws Exception{
        //given, when
        String filePath = fileService.save(getMockUploadFile());
        fileService.delete(filePath);

        //then
        File file = new File(filePath);
        assertThat(file.exists()).isFalse();
    }
}
