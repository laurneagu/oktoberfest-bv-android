package larc.ludiconprod;

/**
 * Created by ancuta on 7/11/2017.
 */

public class DatabaseConnection {

    private static DatabaseConnection instance = null;

    protected DatabaseConnection() {
    }

    public static DatabaseConnection getInstance() {
        if(instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }


   /* public void makeJsonObjectRequest(final Context context) {JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
            urlJsonObj, null, new Response.Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject response) {

            try {
                // Parsing json object response
                // response will be a json object
                String name = response.getString("id");
                String email = response.getString("name");
                String phone = response.getString("email");
                String home = response.getString("fact");

                jsonResponse = "";
                jsonResponse += "Name: " + name + "\n\n";
                jsonResponse += "Email: " + email + "\n\n";
                jsonResponse += "Home: " + home + "\n\n";

                System.out.println(jsonResponse);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(context,
                        "Error: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }

        }
    }, new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(context,
                    error.getMessage(), Toast.LENGTH_SHORT).show();
    }
    });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }
    */


    String tag_json_obj = "json_obj_req";

    //String url = "http://api.androidhive.info/volley/person_object.json";

 /*   public void loginPOST(final Context context,final String email,final String password,String url) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Map<String, Object> result = new HashMap<String, Object>();
                            result.put("authKey", response.getString("authKey"));
                            JSONObject json = response.getJSONObject("user");
                            JSONArray jsonArray = json.getJSONArray("sports");
                            ArrayList<Sport> listOfSports = new ArrayList<Sport>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Sport sport=new Sport(jsonArray.getJSONObject(i).getString("sportName"),
                                        jsonArray.getJSONObject(i).getString("cod"));
                                listOfSports.add(sport);
                            }
                            User user =new User(json.getString("id"), json.getString("firstName"), json.getString("gender"),
                                    json.getString("facebookId"), json.getString("lastName"),
                                    json.getInt("ludicoins"), json.getInt("level"),json.getString("profileImage"),
                                    json.getString("range"), listOfSports);
                            result.put("user", user);
                            String rezultat="";
                            rezultat+= "authkey: " + result.get("authkey").toString() + "\n\n";
                            rezultat+= "Id: " + user.id + "\n\n";
                            rezultat+= "firstName: " + user.firstName + "\n\n";
                            System.out.println("am ajuns");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
            }
        }) {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);

                return params;
            }
            /**
             * Passing some request headers
             * */
         /*   @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("apiKey", "xxxxxxxxxxxxxxx");
                return headers;
            }
            */
        };

     /*   try {
// Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    */
//}
