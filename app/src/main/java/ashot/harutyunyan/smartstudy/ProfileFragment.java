package ashot.harutyunyan.smartstudy;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    TextView tvEmail, tvEmailInfo, tvName, tvAvatar, tvStatus;
    Button btnSignOut;
    FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();

        tvEmail = view.findViewById(R.id.tvEmail);
        tvEmailInfo = view.findViewById(R.id.tvEmailInfo);
        tvName = view.findViewById(R.id.tvName);
        tvAvatar = view.findViewById(R.id.tvAvatar);
        tvStatus = view.findViewById(R.id.tvStatus);
        btnSignOut = view.findViewById(R.id.btnSignOut);

        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String email = user.getEmail();
            String displayName = user.getDisplayName();
            String firstLetter = (displayName != null && !displayName.isEmpty())
                    ? String.valueOf(displayName.charAt(0)).toUpperCase()
                    : String.valueOf(email.charAt(0)).toUpperCase();
            tvName.setText(displayName != null ? displayName : "Welcome back!");
            tvAvatar.setText(firstLetter);
            tvEmail.setText(email);
            tvEmailInfo.setText(email);
            tvStatus.setText("Signed in");
            btnSignOut.setText("Sign Out");
            btnSignOut.setOnClickListener(v -> {
                auth.signOut();
                startActivity(new Intent(getActivity(), SignInActivity.class));
            });
        } else {
            tvAvatar.setText("?");
            tvName.setText("Guest User");
            tvEmail.setText("Not signed in");
            tvEmailInfo.setText("—");
            tvStatus.setText("Guest");
            tvStatus.setTextColor(android.graphics.Color.GRAY);
            btnSignOut.setText("Sign In");
            btnSignOut.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), SignInActivity.class));
            });
        }

        return view;
    }
}