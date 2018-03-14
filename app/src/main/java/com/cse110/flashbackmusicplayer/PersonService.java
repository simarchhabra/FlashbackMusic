package com.cse110.flashbackmusicplayer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;

import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

public class PersonService extends Service {

    private static final String ACTION_PERSON = "GET PERSON";
    public static final String ACTION_BROADCAST = "BROADCAST";
    Intent sendData;
    private String CLIENT_ID;
    String CLIENT_SECRET;
    PeopleService peopleService;
    List<List<String>> contacts;

    @Override
    public void onCreate() {
        CLIENT_ID = getString(R.string.client_id);
        CLIENT_SECRET = getString(R.string.client_secret_id);

        sendData = new Intent(ACTION_BROADCAST);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if (intent.getAction().equals(ACTION_PERSON)) {
            thread.start();
        }
        return START_STICKY;
    }

    Thread thread = new Thread(new Runnable(){
        @Override
        public void run() {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(PersonService.this);
            try {
                peopleService = getPeopleService(account);
                Person profile = peopleService.people().get("people/me")
                        .setPersonFields("names,emailAddresses")
                        .execute();
                String res_name = profile.getResourceName();
                String name = profile.getNames().get(0).getDisplayName();
                String email = profile.getEmailAddresses().get(0).getValue();
                String[] profile_info = {res_name, name, email};
                sendData.putExtra("PROFILE", profile_info);
                contacts = buildConnectionsHandler(peopleService);
                sendData.putExtra("CONTACTS", contacts.toArray());
                sendBroadcast(sendData);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    });

    private PeopleService getPeopleService(GoogleSignInAccount account) throws IOException {
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = new JacksonFactory();
        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(httpTransport, jsonFactory,
                CLIENT_ID, CLIENT_SECRET, account.getServerAuthCode(), "").execute();
        GoogleCredential credential = new GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
                .build()
                .setFromTokenResponse(tokenResponse);
        return new PeopleService.Builder(httpTransport, jsonFactory, credential).build();
    }

    private List<List<String>> buildConnectionsHandler(PeopleService peopleService) {
        List<List<String>> contacts = null;
        try {
            contacts = buildConnections(peopleService);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    private List<List<String>> buildConnections(PeopleService peopleService) throws IOException {
        ListConnectionsResponse response = peopleService.people().connections().list("people/me")
                .setPersonFields("names,emailAddresses")
                .execute();
        List<Person> connections = response.getConnections();
        List<List<String>> contacts = new ArrayList<>();
        if (response.size() > 0) {
            for (Person person : connections) {
                if (!person.isEmpty()) {
                    List<Name> namesList = person.getNames();
                    String name = namesList.get(0).getDisplayName();
                    List<EmailAddress> emailsList = person.getEmailAddresses();
                    String email = emailsList.get(0).getValue();
                    String resName = person.getResourceName();
                    List<String> contact = new ArrayList<>();
                    contact.add(resName);
                    contact.add(name);
                    contact.add(email);
                    contacts.add(contact);
                }
            }
        }
        return contacts;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //no binding necessary
        return null;
    }
}
