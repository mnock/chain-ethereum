package com.tunion.cores;

import com.tunion.SpringBootStartApplication;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SpringBootStartApplication.class})
@AutoConfigureMockMvc
public class BaseTest {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

}
