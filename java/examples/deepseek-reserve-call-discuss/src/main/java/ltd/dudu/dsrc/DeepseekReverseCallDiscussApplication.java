package ltd.dudu.dsrc;

// Copyright (c) 2025 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(
  exclude = {DataSourceAutoConfiguration.class},
  scanBasePackages = {"ltd.dudu.dsrc"}
)
public class DeepseekReverseCallDiscussApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeepseekReverseCallDiscussApplication.class, args);
	}

}
