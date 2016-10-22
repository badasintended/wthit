package au.com.bytecode.opencsv.bean;


import au.com.bytecode.opencsv.CSVReader;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;

/**
 * Copyright 2007 Kyle Miller.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public interface MappingStrategy<T> {

    /**
     * Implementation will have to return a property descriptor from a bean based on the current column.
     *
     * @param col the column to find the description for
     * @return the related PropertyDescriptor
     * @throws java.beans.IntrospectionException
     */
    public abstract PropertyDescriptor findDescriptor(int col) throws IntrospectionException;

    public abstract T createBean() throws InstantiationException, IllegalAccessException;

    /**
     * Implementation of this method can grab the header line before parsing begins to use to map columns
     * to bean properties.
     *
     * @param reader the CSVReader to use for header parsing
     * @throws java.io.IOException if parsing fails
     */
    public void captureHeader(CSVReader reader) throws IOException;

}