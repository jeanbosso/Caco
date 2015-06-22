package br.com.caco.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import br.com.caco.R;
import br.com.caco.br.com.caco.util.Mask;
import br.com.caco.br.com.caco.util.Util;
import br.com.caco.model.User;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class CreateAccountActivity extends Activity{
	String gender;
	private static final String TAG = "HttpClient";

    private TextWatcher cpfMask;
    private TextWatcher dateMask;
    @Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_account);
		
		getActionBar().setTitle(R.string.login_criarconta);
		
		final EditText editEmail = (EditText) findViewById(R.id.editTextCreateAccountEmail);
        final EditText editCpf = (EditText) findViewById(R.id.editTextCreateAccountCpf);
        final EditText editAniversario = (EditText) findViewById(R.id.editTextCreateAccountAniversario);
		Button btnCriar = (Button) findViewById(R.id.buttonCreateAccountEntrar);
		
		String mail ;
		
		mail = getEmail(this, "com.facebook.auth.login");
		
		if(mail != null)
		{
			editEmail.setText(mail);
		}
		else
		{
			mail = getEmail(this, "com.google");
					editEmail.setText(mail);
		}

        cpfMask = Mask.insert("###.###.###-##", editCpf);
        editCpf.addTextChangedListener(cpfMask);
        dateMask = Mask.insert("##/##/####", editAniversario);
        editAniversario.addTextChangedListener(dateMask);

		btnCriar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				EditText editNome = (EditText) findViewById(R.id.editTextCreateAccountNome);
				EditText editCelular = (EditText) findViewById(R.id.editTextCreateAccountCelular);
				EditText editSenha = (EditText) findViewById(R.id.editTextCreateAccountSenha);
                EditText editLogin = (EditText) findViewById(R.id.editTextCreateAccountLogin);

				User user = new User();
                String name = editNome.getText().toString();

                if(name.split("\\w+").length>1){

                    user.setLastName(name.substring(name.lastIndexOf(" ") + 1));
                    user.setFirstName(name.substring(0, name.lastIndexOf(' ')));
                }
                else{
                    user.setFirstName(name);
                }
				user.setCellphone(Long.parseLong(editCelular.getText().toString()));
				user.setEmail(editEmail.getText().toString());
				user.setGender(gender);
				user.setBirthdate(dateStringToMillis(editAniversario.getText().toString()));
                user.setLogin(editLogin.getText().toString());
                user.setCpf(Long.parseLong(editCpf.getText().toString()));



						Intent itMain = new Intent (getApplicationContext(), FidelityCardActivity.class);
						startActivity(itMain);
	
				
				
			}
		});
	}

    public void postData(User user) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://192.168.0.7:8080/Caco-webservice/addUser");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("id", "12345"));
            nameValuePairs.add(new BasicNameValuePair("stringdata", "Hi"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            for (String line = null; (line = reader.readLine()) != null;) {
                builder.append(line).append("\n");
            }
            JSONTokener tokener = new JSONTokener(builder.toString());
            JSONArray finalResult = new JSONArray(tokener);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getEmail (Context ctx, String mail)
	{
		Account[] accounts = AccountManager.get(this).getAccounts();
		String strGmail = null;
	    Log.e("", "Size: " + accounts.length);
	    for (Account account : accounts) {

	        String possibleEmail = account.name;
	        String type = account.type;

	        if (type.equals(mail)) {
	             strGmail = possibleEmail;
	            Log.e("", "Emails: " + strGmail);
	            break;
	        }
	
	    }
	    
	    return strGmail;
	}
	
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radioMale:
	            if (checked)
	                gender = "M";
	            break;
	        case R.id.radioFemale:
	            if (checked)
	               gender = "F";
	            break;
	    }
	}

    public String dateStringToMillis(String date)
    {
        Calendar calendar = Calendar.getInstance();

        List<String> dateSplice = Util.fastSplit(date, '/', false);

        int dayOfMonth = Integer.parseInt(dateSplice.get(0));
        int month =  (Integer.parseInt(dateSplice.get(1))-1);
        int year = Integer.parseInt(dateSplice.get(2));

        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);

        String timeInMillis = ""+calendar.getTimeInMillis();

        return timeInMillis;
    }

}