package br.com.kiks.charades.services;

import br.com.kiks.charades.models.CategoryDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by rsaki on 2/7/2016.
 */
public interface OnlineCategoriesService {
    @GET("categories")
    Call<List<String>> listCategories();

    @GET("categories/{categorySlug}")
    Call<CategoryDto> getCategory(@Path("categorySlug") String categorySlug);
}
