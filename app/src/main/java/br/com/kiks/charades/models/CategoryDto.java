package br.com.kiks.charades.models;

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
    public String imagePath;
    public List<String> tags;
    public List<String> items;

    public static CategoryDto fromModel(CategoryModel model) {
        CategoryDto dto = new CategoryDto();
        ArrayList<String> tags = new ArrayList<>();
        ArrayList<String> items = new ArrayList<>();

        dto.id = model.getId();
        dto.icon = model.getIcon();
        dto.language = model.getLanguage();
        dto.imagePath = model.getImagePath();
        dto.title = model.getTitle();
        dto.tags = tags;
        dto.items = items;

        for (CategoryTagModel tag : model.getTags()) {
            dto.tags.add(tag.getValue());
        }
        for (CategoryItemModel itemModel : model.getItems()) {
            dto.items.add(itemModel.getValue());
        }
        return dto;
    }
}
