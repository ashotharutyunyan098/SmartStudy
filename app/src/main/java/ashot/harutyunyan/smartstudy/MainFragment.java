package ashot.harutyunyan.smartstudy;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

public class MainFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        CardView cardAlgebra = view.findViewById(R.id.cardAlgebra);
        CardView cardGeometry = view.findViewById(R.id.cardGeometry);
        CardView cardMath = view.findViewById(R.id.cardMath);
        CardView cardCalculator = view.findViewById(R.id.cardCalculator);

        cardAlgebra.setOnClickListener(v -> openSubject("Algebra"));
        cardGeometry.setOnClickListener(v -> openSubject("Geometry"));
        cardMath.setOnClickListener(v -> openSubject("Mathematics"));
        cardCalculator.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CalculatorActivity.class));
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });


        animateCards(cardMath, cardAlgebra, cardGeometry, cardCalculator);

        return view;
    }

    private void openSubject(String subject) {
        Intent intent = new Intent(getActivity(), SelectClassActivity.class);
        intent.putExtra("subject", subject);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void animateCards(View... cards) {
        for (int i = 0; i < cards.length; i++) {
            View card = cards[i];
            card.setAlpha(0f);
            card.setTranslationY(100f);
            card.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(600)
                    .setStartDelay(i * 200L)
                    .setInterpolator(new android.view.animation.DecelerateInterpolator())
                    .start();
        }
    }
}