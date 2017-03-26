package feup.cm.traintickets.runnables;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

import feup.cm.traintickets.R;
import feup.cm.traintickets.activities.LoginActivity;
import feup.cm.traintickets.controllers.UserController;
import feup.cm.traintickets.encryption.Encryption;

public abstract class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

    private final String mEmail;
    private final String mPassword;

    public UserLoginTask(String email, String password) {
        mEmail = email;
        mPassword = password;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        UserController userController = new UserController();
        JSONObject object = userController.getUserByEmail(mEmail);

        if(object != null) {
            try {
                return Encryption.compareHashes(object.getString("password"), mPassword);
            } catch (JSONException | NoSuchAlgorithmException ignored) { }
        }
        return false;
    }

    @Override
    protected abstract void onPostExecute(final Boolean success);

    @Override
    protected abstract void onCancelled();
}