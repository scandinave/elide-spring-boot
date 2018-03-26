/*
 * Copyright (c) 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.illyasviel.elide.spring.boot.autoconfigure;

import com.yahoo.elide.Elide;
import com.yahoo.elide.ElideResponse;
import java.security.Principal;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedHashMap;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

/**
 * TODO all request mapping produces should without any media type parameters
 * @author olOwOlo
 */
@Configuration
@AutoConfigureAfter(ElideAutoConfiguration.class)
@ConditionalOnProperty(prefix = "elide.mvc", value = "enable", havingValue = "true", matchIfMissing = true)
public class ElideControllerAutoConfiguration {

  static final String JSON_API_CONTENT_TYPE = "application/vnd.api+json";

  @Configuration
  @RestController
  @RequestMapping(produces = JSON_API_CONTENT_TYPE)
  @ConditionalOnProperty(prefix = "elide.mvc", value = "get", havingValue = "true", matchIfMissing = true)
  public static class ElideGetController {

    private final Elide elide;

    public ElideGetController(Elide elide) {
      this.elide = elide;
    }

    @GetMapping(value = "/**")
    public ResponseEntity<String> elideGet(@RequestParam Map<String, String> allRequestParams,
        HttpServletRequest request, Principal authentication) {
      ElideResponse response = elide
          .get(getJsonAPIPath(request), new MultivaluedHashMap<>(allRequestParams), authentication);
      return ResponseEntity.status(response.getResponseCode()).body(response.getBody());
    }
  }

  @Configuration
  @RestController
  @RequestMapping(produces = JSON_API_CONTENT_TYPE)
  @ConditionalOnProperty(prefix = "elide.mvc", value = "post", havingValue = "true", matchIfMissing = true)
  public static class ElidePostController {

    private final Elide elide;

    public ElidePostController(Elide elide) {
      this.elide = elide;
    }

    @PostMapping(value = "/**", consumes = JSON_API_CONTENT_TYPE)
    public ResponseEntity<String> elidePost(@RequestBody String body,
        HttpServletRequest request, Principal authentication) {
      ElideResponse response = elide.post(getJsonAPIPath(request), body, authentication);
      return ResponseEntity.status(response.getResponseCode()).body(response.getBody());
    }
  }

  @Configuration
  @RestController
  @RequestMapping(produces = JSON_API_CONTENT_TYPE)
  @ConditionalOnProperty(prefix = "elide.mvc", value = "patch", havingValue = "true", matchIfMissing = true)
  public static class ElidePatchController {

    private final Elide elide;

    public ElidePatchController(Elide elide) {
      this.elide = elide;
    }

    @PatchMapping(value = "/**", consumes = JSON_API_CONTENT_TYPE)
    public ResponseEntity<String> elidePatch(@RequestBody String body,
        HttpServletRequest request, Principal authentication) {
      ElideResponse response = elide.patch(JSON_API_CONTENT_TYPE, JSON_API_CONTENT_TYPE,
          getJsonAPIPath(request), body, authentication);
      return ResponseEntity.status(response.getResponseCode()).body(response.getBody());
    }
  }


  @Configuration
  @RestController
  @RequestMapping(produces = JSON_API_CONTENT_TYPE)
  @ConditionalOnProperty(prefix = "elide.mvc", value = "delete", havingValue = "true", matchIfMissing = true)
  public static class ElideDeleteController {

    private final Elide elide;

    public ElideDeleteController(Elide elide) {
      this.elide = elide;
    }

    @DeleteMapping(value = "/**", consumes = JSON_API_CONTENT_TYPE)
    public ResponseEntity<String> elideDeleteRelationship(@RequestBody String body,
        HttpServletRequest request, Principal authentication) {
      ElideResponse response = elide.delete(getJsonAPIPath(request), body, authentication);
      return ResponseEntity.status(response.getResponseCode()).body(response.getBody());
    }

    @DeleteMapping(value = "/**")
    public ResponseEntity<String> elideDelete(HttpServletRequest request,
        Principal authentication) {
      ElideResponse response = elide.delete(getJsonAPIPath(request), null, authentication);
      return ResponseEntity.status(response.getResponseCode()).body(response.getBody());
    }
  }

  private static String getJsonAPIPath(HttpServletRequest request) {
    return ((String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE));
  }
}