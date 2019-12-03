package ir.oveissi.moviesinappbilling;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ir.oveissi.moviesinappbilling.data.DataRepo;

public class MainActivity extends AppCompatActivity {

    private RecyclerView moviesRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moviesRv = findViewById(R.id.movies_rv);
        setupMoviesRv();
    }

    private void setupMoviesRv() {
        MovieAdapter adapter = new MovieAdapter(DataRepo.getMovies());
        moviesRv.setAdapter(adapter);
        moviesRv.setHasFixedSize(true);
        moviesRv.setLayoutManager(new LinearLayoutManager(this));
    }
}
