package Retroapi.APIService;

import Retroapi.Data.RetrofitClient2;

public class ApiUtils2 {

    private ApiUtils2() {}

    public static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

    public static APIService2 getAPIService() {

        return RetrofitClient2.getClient(BASE_URL).create(APIService2.class);
    }
}
