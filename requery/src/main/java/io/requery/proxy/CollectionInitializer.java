/*
 * Copyright 2016 requery.io
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

package io.requery.proxy;

import io.requery.meta.Attribute;
import io.requery.query.Result;
import io.requery.util.ObservableList;
import io.requery.util.ObservableSet;
import io.requery.util.function.Supplier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectionInitializer<E, V> implements Initializer<E, V>,
    QueryInitializer<E, V> {

    @Override
    public V initialize(EntityProxy<E> proxy, Attribute<E, V> attribute) {
        return initialize(proxy, attribute, null);
    }

    @Override
    public <U> V initialize(EntityProxy<E> proxy, Attribute<E, V> attribute,
                            Supplier<Result<U>> query) {
        Class<?> type = attribute.classType();
        CollectionChanges<E, U> changes = new CollectionChanges<>(proxy, attribute);
        Result<U> result = query == null ? null : query.get();
        Collection<U> collection;
        if (type == Set.class) {
            HashSet<U> set = new HashSet<>();
            if (result != null) {
                result.collect(set);
            }
            collection = new ObservableSet<>(set, changes);
        } else if (type == List.class) {
            ArrayList<U> list = new ArrayList<>();
            if (result != null) {
                result.collect(list);
            }
            collection = new ObservableList<>(list, changes);
        } else {
            throw new IllegalStateException("Unsupported collection type " + type);
        }
        return attribute.classType().cast(collection);
    }
}
