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
package com.foreach.across.modules.entity.views.elements.form.dependencies;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * @author Andy Somers
 */
public class Qualifiers {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean checked;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean enabled;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> values;

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
