/** 
 * Cache.java 
 * 
 * Copyright 2013 President and Fellows of Harvard College
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
package org.filteredpush.qc.georeference.util;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cobalt
 * Date: 02.10.2013
 * Time: 14:13
 *
 * @author mole
 * @version $Id: $Id
 */
public interface Cache {
    /**
     * <p>lookup.</p>
     *
     * @param key a {@link java.util.List} object.
     * @return a {@link java.lang.String} object.
     */
    public String lookup(List<String> key);
    /**
     * <p>insert.</p>
     *
     * @param entry a {@link java.util.List} object.
     */
    public void insert(List<String> entry);
}
