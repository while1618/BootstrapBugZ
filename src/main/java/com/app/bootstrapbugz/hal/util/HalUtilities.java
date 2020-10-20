package com.app.bootstrapbugz.hal.util;

import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HalUtilities {
    private HalUtilities() {}

    public static <T, U> CollectionModel<T> mapListOfEntities(List<U> entities, Class<T> mapTo, ModelMapper modelMapper) {
        Collection<T> collection = new ArrayList<>();
        for (U entity : entities) {
            collection.add(modelMapper.map(entity, mapTo));
        }
        return CollectionModel.of(collection);
    }
}
