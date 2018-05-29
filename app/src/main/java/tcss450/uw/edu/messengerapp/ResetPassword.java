package tcss450.uw.edu.messengerapp;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResetPassword extends Fragment {

    private OnResetPasswordFragmentInteractionListener mListener;


    public ResetPassword() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_reset_password, container, false);
        Button b = (Button) v.findViewById(R.id.verifyChangePasswordButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText code = (EditText) v.findViewById(R.id.verifyChangePasswordCode);
                String input = code.getText().toString();
                if (input.trim().length() == 0) {
                    code.setError("Field cannot be empty");
                } else {
                    int inputCode = Integer.parseInt(input);
                    mListener.onVerifyResetButtonInteraction(inputCode);
                }
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ResetPassword.OnResetPasswordFragmentInteractionListener) {
            mListener = (ResetPassword.OnResetPasswordFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnResetPasswordFragmentInteractionListener");
        }
    }

    public void handleVerifyOnPre() {
        Button b = getView().findViewById(R.id.verifyChangePasswordButton);
        b.setEnabled(false);

        EditText et = getView().findViewById(R.id.verifyChangePasswordCode);
        et.setEnabled(false);

        ImageView redx = getView().findViewById(R.id.redx);
        ImageView checkmark = getView().findViewById(R.id.checkmark);

        if (redx.getVisibility() == View.VISIBLE) {
            redx.setVisibility(ImageView.GONE);
        }

        if (checkmark.getVisibility() == View.VISIBLE) {
            checkmark.setVisibility(ImageView.GONE);
        }


        ProgressBar progBar = getView().findViewById(R.id.verifyResetProgressBar);
        progBar.setVisibility(ProgressBar.VISIBLE);
    }

    public void handleResetOnPre() {
        EditText enter = getView().findViewById(R.id.changePasswordEditText);
        enter.setEnabled(false);

        EditText conf = getView().findViewById(R.id.confirmChangePasswordEditText);
        conf.setEnabled(false);

        Button b = getView().findViewById(R.id.resetPasswordButton);
        b.setEnabled(false);

        ProgressBar progBar = getView().findViewById(R.id.resetPasswordProgressBar);
        progBar.setVisibility(ProgressBar.VISIBLE);
    }

    public void handleVerifyOnPost(boolean success) {
        ProgressBar progBar = getView().findViewById(R.id.verifyResetProgressBar);
        progBar.setVisibility(ProgressBar.GONE);

        ImageView redx = getView().findViewById(R.id.redx);

        if (success) {
            if (redx.getVisibility() == View.VISIBLE) {
                redx.setVisibility(ImageView.GONE);
            }

            ImageView checkmark = getView().findViewById(R.id.checkmark);
            checkmark.setVisibility(ImageView.VISIBLE);

            TextView tv = getView().findViewById(R.id.resetAndConfirmTextView);
            tv.setVisibility(TextView.VISIBLE);

            EditText enter = getView().findViewById(R.id.changePasswordEditText);
            enter.setVisibility(EditText.VISIBLE);

            TextView instr = getView().findViewById(R.id.changePassInstructionsText);
            instr.setVisibility(TextView.VISIBLE);

            EditText confirm = getView().findViewById(R.id.confirmChangePasswordEditText);
            confirm.setVisibility(EditText.VISIBLE);

            Button b = getView().findViewById(R.id.resetPasswordButton);
            b.setVisibility(Button.VISIBLE);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onResetButtonClick();
                }
            });
        } else {
            redx.setVisibility(ImageView.VISIBLE);

            EditText et = getView().findViewById(R.id.verifyChangePasswordCode);
            et.setEnabled(true);
            et.setError("Code was not matching");

            Button b = getView().findViewById(R.id.verifyChangePasswordButton);
            b.setEnabled(true);
        }
    }

    public void handleOnError() {
        EditText enter = getView().findViewById(R.id.changePasswordEditText);
        enter.setEnabled(true);

        EditText conf = getView().findViewById(R.id.confirmChangePasswordEditText);
        conf.setEnabled(true);

        Button b = getView().findViewById(R.id.resetPasswordButton);
        b.setEnabled(true);

        ProgressBar progBar = getView().findViewById(R.id.resetPasswordProgressBar);
        progBar.setVisibility(ProgressBar.GONE);

        Toast.makeText(getActivity(), "Something went wrong in the backend",
                Toast.LENGTH_SHORT).show();
    }

    private void onResetButtonClick() {
        boolean meetsConstraints = passMeetsConstraints();
        boolean isNotEmpty = checkIsEmpty();
        boolean passwordsMatch = passwordsMatch();

        if (isNotEmpty && meetsConstraints && passwordsMatch) {
            EditText password = getView().findViewById(R.id.changePasswordEditText);
            Editable passwordString = password.getEditableText();
            mListener.onResetButtonInteraction(passwordString);
        }
    }

    boolean checkIsEmpty() {
        boolean fieldOneIsEmpty = true;
        boolean fieldTwoIsEmpty = true;

        EditText password = getView().findViewById(R.id.changePasswordEditText);
        String passwordString = password.getText().toString();

        EditText confPassword = getView().findViewById(R.id.confirmChangePasswordEditText);
        String confPasswordString = password.getText().toString();

        if (passwordString.trim().length() == 0) {
            password.setError("Field cannot be empty");
        } else {
            fieldOneIsEmpty = false;
        }

        if (confPasswordString.trim().length() == 0) {
            confPassword.setError("Field cannot be empty");
        } else {
            fieldTwoIsEmpty = false;
        }

        if (!(fieldOneIsEmpty && fieldTwoIsEmpty)) {
            return true;
        }

        return false;
    }

    public boolean passMeetsConstraints() {
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasLength = false;

        boolean meetsConstraints = false;

        EditText password = getView().findViewById(R.id.changePasswordEditText);
        String passwordString = password.getText().toString();

        if (passwordString.trim().length() < 5) {
            password.setError("Password must be at least 5 characters");
        } else {
            hasLength = true;
        }

        for (int i = 0; i < passwordString.length(); i++) {
            if (Character.isUpperCase(passwordString.charAt(i))) {
                hasUpper = true;
            }
        }

        if (!hasUpper) {
            password.setError("Password must contain upper case letter");
        }

        for (int i = 0; i < passwordString.length(); i++) {
            if (Character.isDigit(passwordString.charAt(i))) {
                hasDigit = true;
            }
        }

        if (!hasDigit) {
            password.setError("Password must contain one digit");
        }

        if (hasDigit && hasLength && hasUpper) {
            meetsConstraints = true;
        }

        return meetsConstraints;
    }

    public boolean passwordsMatch() {
        boolean isMatching;

        EditText passwordOne = getView().findViewById(R.id.changePasswordEditText);
        EditText passwordTwo = getView().findViewById(R.id.confirmChangePasswordEditText);
        String passwordOneString = passwordOne.getText().toString();
        String passwordTwoString = passwordTwo.getText().toString();
        if (passwordOneString.equals(passwordTwoString)) {
            isMatching = true;
        } else {
            isMatching = false;
            passwordOne.setError("Passwords must be matching");
            passwordTwo.setError("Passwords must be matching");
        }

        return isMatching;
    }

    public interface OnResetPasswordFragmentInteractionListener {
        void onVerifyResetButtonInteraction(int code);
        void onResetButtonInteraction(Editable pass);
    }

}
