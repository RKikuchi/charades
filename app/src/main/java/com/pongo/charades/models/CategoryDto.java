package com.pongo.charades.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rsaki on 2/7/2016.
 */
public class CategoryDto {
    public int id;
    public String icon;
    public String language;
    public String title;
    public List<CategoryItemDto> items;

    public static CategoryDto fromModel(CategoryModel model) {
        CategoryDto dto = new CategoryDto();
        ArrayList<CategoryItemDto> items = new ArrayList<>();

        dto.id = model.getId();
        dto.icon = model.getIcon();
        dto.language = model.getLanguage();
        dto.title = model.getTitle();
        dto.items = items;

        for (CategoryItemModel itemModel : model.getItems()) {
            dto.items.add(CategoryItemDto.fromModel(itemModel));
        }
        return dto;
    }
}
