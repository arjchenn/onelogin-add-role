package app.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Main {
	private String token;

	public class RootObject // This is the main object
	{
		public Status status;
		public List<Datum> data;
		public pagination pagination;

		public Status getStatus() {
			return status;
		}

		public void setStatus(Status status) {
			this.status = status;
		}

		public List<Datum> getData() {
			return data;
		}

		public void setData(List<Datum> data) {
			this.data = data;
		}

		public pagination getPagination() {
			return pagination;
		}

		public void setPagination(pagination pagination) {
			this.pagination = pagination;
		}
	}

	public class Status // This is the jSON status
	{
		public Boolean error;
		public int code;
		public String type;
		public String message;

		public Boolean getError() {
			return error;
		}

		public void setError(Boolean error) {
			this.error = error;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}

	public class Datum // This is the actual data that is returned
	{
		public String access_token;
		public String created_at;
		public int expires_in;
		public String refresh_token;
		public String token_type;
		public int account_id;
		public int id;
		public String email;
		public custom_attributes custom_attributes;
		public String name;

		public String getAccess_token() {
			return access_token;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}

		public String getCreated_at() {
			return created_at;
		}

		public void setCreated_at(String created_at) {
			this.created_at = created_at;
		}

		public int getExpires_in() {
			return expires_in;
		}

		public void setExpires_in(int expires_in) {
			this.expires_in = expires_in;
		}

		public String getRefresh_token() {
			return refresh_token;
		}

		public void setRefresh_token(String refresh_token) {
			this.refresh_token = refresh_token;
		}

		public String getToken_type() {
			return token_type;
		}

		public void setToken_type(String token_type) {
			this.token_type = token_type;
		}

		public int getAccount_id() {
			return account_id;
		}

		public void setAccount_id(int account_id) {
			this.account_id = account_id;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public custom_attributes getCustom_attributes() {
			return custom_attributes;
		}

		public void setCustom_attributes(custom_attributes custom_attributes) {
			this.custom_attributes = custom_attributes;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public class pagination {
		public String next_link;
		public String previous_link;
		public String before_cursor;
		public String after_cursor;

		public String getNext_link() {
			return next_link;
		}

		public void setNext_link(String next_link) {
			this.next_link = next_link;
		}

		public String getPrevious_link() {
			return previous_link;
		}

		public void setPrevious_link(String previous_link) {
			this.previous_link = previous_link;
		}

		public String getBefore_cursor() {
			return before_cursor;
		}

		public void setBefore_cursor(String before_cursor) {
			this.before_cursor = before_cursor;
		}

		public String getAfter_cursor() {
			return after_cursor;
		}

		public void setAfter_cursor(String after_cursor) {
			this.after_cursor = after_cursor;
		}

	}

	public class custom_attributes {
		public String customAttribute1;
		public String customAttribute2;

	}

	public String getRole(int roleId) throws IOException, URISyntaxException {

		Gson gson = new GsonBuilder().create();
		URIBuilder uriBuilder = new URIBuilder("https://api.us.onelogin.com/api/1/roles/" + Integer.toString(roleId));
		System.out.println(uriBuilder);
		HttpGet postRequest = new HttpGet(uriBuilder.build());
		postRequest.addHeader("cache-control", "no-cache");
		postRequest.addHeader("authorization", "bearer:" + token + "");

		// Build the http client.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = httpclient.execute(postRequest);

		// Read the response
		String responseString = "";

		HttpEntity responseHttpEntity = response.getEntity();

		InputStream content = responseHttpEntity.getContent();

		BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
		String line;

		while ((line = buffer.readLine()) != null) {
			responseString += line;
		}

		System.out.println(responseString);

		// release all resources held by the responseHttpEntity
		EntityUtils.consume(responseHttpEntity);

		// close the stream
		response.close();

		RootObject returnData = gson.fromJson(responseString, RootObject.class);

		if (returnData.data.size() != 0)
			return returnData.data.get(0).name;
		else
			return null;
	}

	@SuppressWarnings("unchecked")
	public Boolean addRole(int roleId, int userId) throws URISyntaxException, ClientProtocolException, IOException {

		JSONObject obj = new JSONObject();

		JSONArray list = new JSONArray();
		list.add(roleId);

		obj.put("role_id_array", list);
		URIBuilder uriBuilder = new URIBuilder(
				"https://api.us.onelogin.com/api/1/users/" + Integer.toString(userId) + "/add_roles");
		System.out.println(uriBuilder);
		HttpPut postRequest = new HttpPut(uriBuilder.build());
		postRequest.addHeader("cache-control", "no-cache");
		postRequest.addHeader("content-type", "application/json");
		postRequest.addHeader("authorization", "bearer:" + token + "");
		System.out.println(obj.toJSONString());

		// pass the json string request in the entity
		HttpEntity entity = new ByteArrayEntity(obj.toJSONString().getBytes("UTF-8"));
		System.out.println(entity.toString());
		postRequest.setEntity(entity);

		// pass the json string request in the entity

		// Build the http client.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = httpclient.execute(postRequest);

		// Read the response
		String responseString = "";

		HttpEntity responseHttpEntity = response.getEntity();

		InputStream content = responseHttpEntity.getContent();

		BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
		String line;

		while ((line = buffer.readLine()) != null) {
			responseString += line;
		}

		System.out.println(responseString);

		// release all resources held by the responseHttpEntity
		EntityUtils.consume(responseHttpEntity);

		// close the stream
		response.close();

		Gson gson = new GsonBuilder().create();
		RootObject returnData = gson.fromJson(responseString, RootObject.class);

		if (returnData.status.error != null)
			return returnData.status.error;
		else
			return true;

	}

	public int getId(String in) throws URISyntaxException, ClientProtocolException, IOException {

		String name = in.toLowerCase();
		System.out.println(name);
		Gson gson = new GsonBuilder().create();
		URIBuilder uriBuilder = new URIBuilder("https://api.us.onelogin.com/api/1/users?username=" + name);
		System.out.println(uriBuilder);
		HttpGet postRequest = new HttpGet(uriBuilder.build());
		postRequest.addHeader("cache-control", "no-cache");
		// postRequest.addHeader("content-type", "application/json");
		postRequest.addHeader("authorization", "bearer:" + token + "");

		// pass the json string request in the entity

		// Build the http client.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = httpclient.execute(postRequest);

		// Read the response
		String responseString = "";

		HttpEntity responseHttpEntity = response.getEntity();

		InputStream content = responseHttpEntity.getContent();

		BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
		String line;

		while ((line = buffer.readLine()) != null) {
			responseString += line;
		}

		System.out.println(responseString);

		// release all resources held by the responseHttpEntity
		EntityUtils.consume(responseHttpEntity);

		// close the stream
		response.close();

		RootObject returnData = gson.fromJson(responseString, RootObject.class);

		if (returnData.data.size() != 0)
			return returnData.data.get(0).id;
		else
			return getIdEmail(in);

	}

	public int getIdEmail(String in) throws URISyntaxException, ClientProtocolException, IOException {

		String name = in.toLowerCase();
		System.out.println("Trying email... " + name);
		Gson gson = new GsonBuilder().create();
		URIBuilder uriBuilder = new URIBuilder("https://api.us.onelogin.com/api/1/users?email=" + name + "*");
		System.out.println(uriBuilder);
		HttpGet postRequest = new HttpGet(uriBuilder.build());
		postRequest.addHeader("cache-control", "no-cache");
		// postRequest.addHeader("content-type", "application/json");
		postRequest.addHeader("authorization", "bearer:" + token + "");

		// pass the json string request in the entity

		// Build the http client.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = httpclient.execute(postRequest);

		// Read the response
		String responseString = "";

		HttpEntity responseHttpEntity = response.getEntity();

		InputStream content = responseHttpEntity.getContent();

		BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
		String line;

		while ((line = buffer.readLine()) != null) {
			responseString += line;
		}

		System.out.println(responseString);

		// release all resources held by the responseHttpEntity
		EntityUtils.consume(responseHttpEntity);

		// close the stream
		response.close();

		RootObject returnData = gson.fromJson(responseString, RootObject.class);

		if (returnData.data.size() != 0)
			return returnData.data.get(0).id;
		else
			return -1;

	}

	public Boolean getToken(String id, String secret) throws ClientProtocolException, IOException, URISyntaxException {
		// Getting a token is rather simple
		// Using a class based variable is probably a good idea since this is
		// only set once but remains constant
		// If this is not desired then the token will need to be carried via
		// other methods
		// Reuse of this value will be necessary among all your calls
		// Use this method first because none of the other calls can work
		// without your access toekn
		Gson gson = new GsonBuilder().create();
		URIBuilder uriBuilder = new URIBuilder("https://api.us.onelogin.com/auth/oauth2/token");
		System.out.println(uriBuilder);
		HttpPost postRequest = new HttpPost(uriBuilder.build());
		postRequest.addHeader("cache-control", "no-cache");
		postRequest.addHeader("content-type", "application/json");
		postRequest.addHeader("authorization", "client_id:" + id + ", client_secret:" + secret);
		// this is your JSON string you are sending as a request
		String yourJsonString = "{\n\"grant_type\":\"client_credentials\"\n} ";

		// pass the json string request in the entity
		HttpEntity entity = new ByteArrayEntity(yourJsonString.getBytes("UTF-8"));
		postRequest.setEntity(entity);

		// Build the http client.
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = httpclient.execute(postRequest);

		// Read the response
		String responseString = "";

		HttpEntity responseHttpEntity = response.getEntity();

		InputStream content = responseHttpEntity.getContent();

		BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
		String line;

		while ((line = buffer.readLine()) != null) {
			responseString += line;
		}

		System.out.println(responseString);

		// release all resources held by the responseHttpEntity
		EntityUtils.consume(responseHttpEntity);

		// close the stream
		response.close();

		RootObject returnData = gson.fromJson(responseString, RootObject.class);
		if (returnData.data.get(0).access_token != null) {
			// This correctly gets the Access Token. You should return this to a
			// class variable so that all the
			// other functions can access it easily and you're not constantly
			// passing along the variable through them.
			this.token = returnData.data.get(0).access_token;
			return true;
		} else
			return false;

	}

}
