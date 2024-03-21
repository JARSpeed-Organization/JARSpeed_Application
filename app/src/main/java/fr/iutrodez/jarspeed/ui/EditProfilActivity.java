package fr.iutrodez.jarspeed.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.example.jarspeed.R;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import fr.iutrodez.jarspeed.model.gender.Gender;
import fr.iutrodez.jarspeed.model.user.User;
import fr.iutrodez.jarspeed.model.user.UserUpdateRequest;
import fr.iutrodez.jarspeed.network.ApiUtils;
import fr.iutrodez.jarspeed.utils.SharedPreferencesManager;
import fr.iutrodez.jarspeed.utils.ValidationUtils;
import fr.iutrodez.jarspeed.utils.PasswordEncryptor;

import com.android.volley.VolleyError;

/**
 * The type Edit profil activity.
 */
public class EditProfilActivity extends AppCompatActivity {

    /**
     * The Edit text birthdate.
     */
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextEmail;

    private Spinner spinnerGender;
    private User user;

    private String oldPasswordBd;
    /**
     * On create.
     *
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profil_activity);

        // Initialisation des vues
        editTextFirstName = findViewById(R.id.firstNameItem);
        editTextLastName = findViewById(R.id.nameItem);
        editTextEmail = findViewById(R.id.emailItem);

        loadUserProfile();

    }

    private void loadUserProfile() {
        String token = SharedPreferencesManager.getAuthToken(this);
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Vous n'êtes pas connecté.", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiUtils.loadUserProfile(this, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    user = new User();
                    user.setFirstname(jsonResponse.optString("firstname"));
                    user.setLastname(jsonResponse.optString("lastname"));
                    user.setEmail(jsonResponse.optString("email"));
                    oldPasswordBd = jsonResponse.optString("password");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        user.setBirthday(dateFormat.parse(jsonResponse.optString("birthdate")));
                    } catch (ParseException pE) {
                        user.setBirthday(new Date());
                    }
                    user.setWeight(jsonResponse.optDouble("weight"));
                    JSONObject genderJSON = jsonResponse.optJSONObject("gender");
                    Gender gender = new Gender();
                    gender.setId(genderJSON.optInt("id"));
                    gender.setLabel(genderJSON.optString("label"));
                    user.setGender(gender);

                    // Mise à jour de l'interface utilisateur avec les données reçues
                    editTextFirstName.setText(user.getFirstname());
                    editTextLastName.setText(user.getLastname());
                    editTextEmail.setText(user.getEmail());
                } catch (JSONException e) {
                    Log.e("LoadUserProfile", "Error parsing JSON", e);
                    Toast.makeText(EditProfilActivity.this, "Erreur lors du parsing des données", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LoadUserProfile", "Error loading profile: " + error.toString());
                Toast.makeText(EditProfilActivity.this, "Erreur lors du chargement du profil", Toast.LENGTH_SHORT).show();
            }
        });
    }



    /**
     * Return to profil page.
     *
     * @param view the view
     */
    public void returnToProfilPage(View view) {
        // Intent pour ouvrir la vue profil
        Intent intent = new Intent(this, ProfilActivity.class);
        startActivity(intent);
    }


    /**
     * On change password click.
     *
     * @param view the view
     */
    public void onChangePasswordClick(View view) {
        loadUserProfile(); // pour récupérer le mot de passe si il a déjà été changé

        // Assombrir l'arrière-plan
        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        final Drawable originalBackground = rootView.getBackground();

        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.2f;
        getWindow().setAttributes(params);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.password_dialog, null);
        builder.setView(dialogView);

        // Edits texts de la popup
        EditText editTextOldPassword = dialogView.findViewById(R.id.editTextOldPassword);
        EditText editTextPassword = dialogView.findViewById(R.id.editTextPassword);
        EditText editTextConfirmPassword = dialogView.findViewById(R.id.editTextConfirmPassword);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        Button buttonConfirm = dialogView.findViewById(R.id.buttonConfirm);

        AlertDialog dialog = builder.create();

        buttonCancel.setOnClickListener(v -> {
            dialog.dismiss();
            // Restaurer l'arrière-plan à la fermeture de la boîte de dialogue
            params.alpha = 1.0f;
            getWindow().setAttributes(params);
        });
        buttonConfirm.setOnClickListener(v -> {
            String oldPassword = PasswordEncryptor.encryptPassword(editTextOldPassword.getText().toString());
            String newPassword = PasswordEncryptor.encryptPassword(editTextPassword.getText().toString());
            String confirmPassword = PasswordEncryptor.encryptPassword(editTextConfirmPassword.getText().toString());

            // vérifier que le champ "ancien mot de passe" est correctement saisi
            if (!oldPassword.equals(oldPasswordBd)) {
                Toasty.error(this, "L'ancien mot de passe saisi est incorrect", Toast.LENGTH_LONG, true).show();
                editTextPassword.requestFocus();
                return;
            }
            // Valider le nouveau mot de passe
            if (!ValidationUtils.isValidPassword(newPassword)) {
                Toasty.warning(this, "Le mot de passe doit contenir au moins 8 caractères, dont une majuscule, un chiffre et un caractère spécial.", Toast.LENGTH_LONG, true).show();
                editTextPassword.requestFocus();
                return;
            }

            // Vérifier que les mots de passe correspondent
            if (!newPassword.equals(confirmPassword)) {
                Toasty.error(this, "Les mots de passe ne correspondent pas.", Toast.LENGTH_LONG, true).show();
                editTextConfirmPassword.requestFocus();
                return;
            }

            // Vérifier que l'ancien mot de passe est différent du nouveau
            if (oldPasswordBd.equals(newPassword)) {
                Toasty.error(this, "Le nouveau mot de passe doit être différent de l'ancien.", Toast.LENGTH_LONG, true).show();
                editTextPassword.requestFocus();
                return;
            }

            // Si tout est valide, créez la demande de mise à jour et envoyez-la
            UserUpdateRequest updateRequest = new UserUpdateRequest();
            updateRequest.setPassword(newPassword); // Assurez-vous que le mot de passe est haché côté serveur

            sendUpdateRequest(updateRequest);

            dialog.dismiss();
            // Restaurer l'arrière-plan à la fermeture de la boîte de dialogue
            rootView.setBackground(originalBackground);
            params.alpha = 1.0f;
            getWindow().setAttributes(params);
        });


        dialog.setOnDismissListener(d -> {
            // Restaurer l'arrière-plan à la fermeture de la boîte de dialogue
            rootView.setBackground(originalBackground);
            params.alpha = 1.0f;
            getWindow().setAttributes(params);
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    /**
     * On generic edit click.
     *
     * @param view the view
     */
    public void onGenericEditClick(View view) {
        // Assombrir l'arrière-plan
        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        final Drawable originalBackground = rootView.getBackground();
        final EditText targetEditText = (EditText) view; // Conserver une référence au EditText cliqué


        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.2f;
        getWindow().setAttributes(params);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.generic_edit_dialog, null);
        builder.setView(dialogView);

        EditText editTextGeneric = dialogView.findViewById(R.id.editTextGeneric);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancelGeneric);
        Button buttonConfirm = dialogView.findViewById(R.id.buttonConfirmGeneric);
        boolean isEmailField = view.getId() == R.id.emailItem;

        String typeModification = "";
        // Configurez le hint de l'EditText en fonction du type de modification
        if (view.getId() == R.id.nameItem) {
            editTextGeneric.setHint("Nouveau nom");
            typeModification = "lastname";
        } else if (view.getId() == R.id.firstNameItem) {
            editTextGeneric.setHint("Nouveau prénom");
            typeModification = "firstname";
        } else {
            editTextGeneric.setHint("Nouvel email");
            typeModification = "email";
        }
        AlertDialog dialog = builder.create();

        buttonCancel.setOnClickListener(v -> {
            dialog.dismiss();
            params.alpha = 1.0f;
            getWindow().setAttributes(params);
        });

        String finalTypeModification = typeModification;
        buttonConfirm.setOnClickListener(v -> {
            String newValue = editTextGeneric.getText().toString().trim();
            if (!newValue.isEmpty() && (!isEmailField || ValidationUtils.isValidEmail(newValue))) {
                // Mettre à jour la valeur du EditText concerné
                targetEditText.setText(newValue);
                // Appeler la méthode de mise à jour avec le champ spécifique et la nouvelle valeur
                sendUpdateRequestBasedOnField(view.getId(), newValue);
                dialog.dismiss();
            } else {
                Toasty.warning(this, "Veuillez entrer une valeur valide.", Toast.LENGTH_SHORT, true).show();
                if (isEmailField) editTextGeneric.setError("Format de l'email invalide");
            }
        });


        dialog.setOnDismissListener(d -> {
            rootView.setBackground(originalBackground);
            params.alpha = 1.0f;
            getWindow().setAttributes(params);
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    /**
     * On change health data.
     *
     * @param view the view
     */
    public void onChangeHealthData(View view) {
        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        final Drawable originalBackground = rootView.getBackground();

        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.2f;
        getWindow().setAttributes(params);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.health_data_dialog, null);
        builder.setView(dialogView);

        TextView textViewBirthdate = dialogView.findViewById(R.id.textViewBirthdate);
        spinnerGender = dialogView.findViewById(R.id.spinnerGender);
        EditText editTextWeight = dialogView.findViewById(R.id.editTextWeight);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancelHealthData);
        Button buttonConfirm = dialogView.findViewById(R.id.buttonConfirmHealthData);

        // Peupler le Spinner avec les genres dès que les données sont disponibles
        ApiUtils.fetchGenders(this, genders -> {
            ArrayAdapter<Gender> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genders);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerGender.setAdapter(adapter);
            // Pré-sélectionner le genre de l'utilisateur
            Log.e("gender", user.getGender().getId().toString());
            spinnerGender.setSelection(getGenderIndex(user.getGender()));
        }, error -> Toasty.error(this, "Erreur lors de la récupération des genres", Toast.LENGTH_SHORT, true).show());



        // Initialize data with data base
        editTextWeight.setText(user.getWeight().toString());

        final Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        textViewBirthdate.setText(dateFormat.format(user.getBirthday()));
        textViewBirthdate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(EditProfilActivity.this, (view1, year, monthOfYear, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String selectedDate = dateFormat.format(calendar.getTime());
                textViewBirthdate.setText(selectedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        AlertDialog dialog = builder.create();
        setupDialogButtons(dialog, buttonCancel, buttonConfirm, textViewBirthdate, editTextWeight, spinnerGender);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

    }

    /**
     * Sets dialog buttons.
     *
     * @param dialog            the dialog
     * @param buttonCancel      the button cancel
     * @param buttonConfirm     the button confirm
     * @param textViewBirthdate the text view birthdate
     * @param editTextWeight    the edit text weight
     * @param spinnerGender     the spinner gender
     */
    private void setupDialogButtons(AlertDialog dialog, Button buttonCancel, Button buttonConfirm, TextView textViewBirthdate, EditText editTextWeight, Spinner spinnerGender) {
        dialog.setOnDismissListener(d -> {
            resetDialogBackground();
        });

        buttonConfirm.setOnClickListener(v -> {
            performHealthDataUpdate(textViewBirthdate, editTextWeight, spinnerGender, dialog);
        });

        buttonCancel.setOnClickListener(v -> {
            dialog.dismiss();
            resetDialogBackground();
        });
    }

    private void darkenBackground() {
        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.2f;
        getWindow().setAttributes(params);
    }
    /**
     * Reset dialog background.
     */
    private void resetDialogBackground() {
        final View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        final Drawable originalBackground = rootView.getBackground();
        final WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 1.0f;
        getWindow().setAttributes(params);
    }

    /**
     * Perform health data update.
     *
     * @param textViewBirthdate the text view birthdate
     * @param editTextWeight    the edit text weight
     * @param spinnerGender     the spinner gender
     * @param dialog            the dialog
     */
    private void performHealthDataUpdate(TextView textViewBirthdate, EditText editTextWeight, Spinner spinnerGender, AlertDialog dialog) {
        try {
            double weight = Double.parseDouble(editTextWeight.getText().toString());
            String birthdate = textViewBirthdate.getText().toString(); // Directement le String

            // Récupération de l'objet Gender sélectionné dans le Spinner
            Gender selectedGender = (Gender) spinnerGender.getSelectedItem();

            UserUpdateRequest updateRequest = new UserUpdateRequest();
            updateRequest.setBirthdate(birthdate);
            updateRequest.setWeight(weight);
            updateRequest.setGender(selectedGender); // Directement l'objet Gender

            Gson gson = new Gson();
            String json = gson.toJson(updateRequest);
            Log.d("UpdateRequest", "Sending Update Request: " + json);

            sendUpdateRequest(updateRequest);

            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            user.setWeight(weight);
            user.setGender(selectedGender);
            try {
                user.setBirthday(dateFormat.parse(birthdate));
            } catch (ParseException pE) {
                Log.e("Save", "Load new information");
            }
            dialog.dismiss();
        } catch (NumberFormatException e) {
            Toasty.warning(this, "Veuillez entrer des données valides.", Toast.LENGTH_SHORT, true).show();
        }
    }


    /**
     * Send update request.
     *
     * @param updateRequest the update request
     */
    private void sendUpdateRequest(UserUpdateRequest updateRequest) {
        ApiUtils.updateUser(this, updateRequest, response -> {
            Toasty.success(EditProfilActivity.this, "Mise à jour réussie", Toast.LENGTH_SHORT, true).show();
        }, error -> {
            Toasty.error(EditProfilActivity.this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT, true).show();
        });
    }

    /**
     * Send update request based on field.
     *
     * @param fieldId  the field id
     * @param newValue the new value
     */
    private void sendUpdateRequestBasedOnField(int fieldId, String newValue) {
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        if (fieldId == R.id.nameItem) {
            updateRequest.setLastname(newValue);
        } else if (fieldId == R.id.firstNameItem) {
            updateRequest.setFirstname(newValue);
        } else if (fieldId == R.id.emailItem) {
            updateRequest.setEmail(newValue);

        }
        sendUpdateRequest(updateRequest);
    }

    private int getGenderIndex(Gender userGender) {
        // Parcourir la liste des genres pour trouver l'index du genre de l'utilisateur
        for (int i = 0; i < spinnerGender.getCount(); i++) {
            Gender gender = (Gender) spinnerGender.getItemAtPosition(i);
            Log.e("gender", gender.getId() + " " + gender.getLabel());
            if (gender.getId() == userGender.getId()) {
                return i; // Retourner l'index lorsque le genre correspond
            }
        }
        return 0; // Par défaut, retourner 0 si le genre de l'utilisateur n'est pas trouvé
    }
}