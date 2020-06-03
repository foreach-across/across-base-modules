/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package test.app.application;

import com.foreach.across.modules.spring.security.annotations.CurrentSecurityPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import test.TestApplicationWithBootAuthenticationManager;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@Controller
public class HomeController
{
	@GetMapping("/hello")
	@ResponseBody
	public String hello() {
		return "hello";
	}

	@GetMapping("/hello-public")
	@ResponseBody
	public String helloPublic() {
		return "hello-public";
	}

	@GetMapping("/blocked")
	@ResponseBody
	public String blocked() {
		return "should-always-be-refused";
	}

	@GetMapping("/thymeleaf-extras")
	public String thymeleafExtras() {
		return "th/springSecurityTest/thymeleaf-extras";
	}

	@GetMapping("/current-user")
	@ResponseBody
	public String currentUser( @CurrentSecurityPrincipal TestApplicationWithBootAuthenticationManager.User user ) {
		if ( user == null ) {
			return "unknown";
		}
		else {
			return user.getPrincipalName() + ":" + user.getFirstName() + ":" + user.getLastName();
		}
	}
}
