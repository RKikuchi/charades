package com.pongo.charades.async;

import android.os.AsyncTask;
import android.util.Log;

import com.pongo.charades.models.CategoryDto;
import com.pongo.charades.models.CategoryItemDto;
import com.pongo.charades.models.CategoryItemModel;
import com.pongo.charades.models.CategoryModel;
import com.pongo.charades.services.OnlineCategoriesService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by rsaki on 2/7/2016.
 */
public class OnlineCategoriesLoader {
    public class LoaderTask extends AsyncTask<Void, Void, List<CategoryModel>> {
        private final OnlineCategoriesLoaderCallback mCallback;

        public LoaderTask(OnlineCategoriesLoaderCallback callback) {
            mCallback = callback;
        }

        @Override
        protected List<CategoryModel> doInBackground(Void... params) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);
            httpClient.networkInterceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request().newBuilder().addHeader("Accept", "application/json").build();
                    return chain.proceed(request);
                }
            });

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://restazero.com/charades/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

            List<String> categoryPaths = null;
            OnlineCategoriesService svc = retrofit.create(OnlineCategoriesService.class);
            try {
                categoryPaths = svc.listCategories().execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (categoryPaths == null) return null;

            List<CategoryModel> result = new ArrayList<CategoryModel>();
            for (String path : categoryPaths) {
                String categorySlug = path.substring(path.lastIndexOf('/') + 1);
                Log.i("CategoriesLoader", "Fetching category " + categorySlug);
                try {
                    CategoryDto dto = svc.getCategory(categorySlug).execute().body();
                    CategoryModel model = new CategoryModel();
                    model.setIsCustom(false);
                    model.setTitle(dto.title);
                    for (CategoryItemDto itemDto : dto.items) {
                        model.getItems().add(new CategoryItemModel(itemDto.value, itemDto.definition));
                    }
                    result.add(model);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(List<CategoryModel> categories) {
            mCallback.categoriesReceived(categories);
        }
    }

    public interface OnlineCategoriesLoaderCallback {
        void categoriesReceived(List<CategoryModel> categories);
    }

    public void load(OnlineCategoriesLoaderCallback callback) {
        new LoaderTask(callback).execute();
    }
}