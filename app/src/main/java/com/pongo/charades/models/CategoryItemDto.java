package com.pongo.charades.models;

/**
 * Created by rsaki on 2/7/2016.
 */
public class CategoryItemDto {
    public String value;
    public String definition;

    public static CategoryItemDto fromModel(CategoryItemModel model) {
        CategoryItemDto dto = new CategoryItemDto();
        dto.value = model.getValue();
        dto.definition = model.getDefinition();
        return  dto;
    }
}
