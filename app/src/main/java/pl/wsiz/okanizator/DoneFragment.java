package pl.wsiz.okanizator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DoneFragment extends Fragment {

    private View v;
    private RecyclerView recyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference DoneRef = db.collection("Done");
    CollectionReference InProgRef = db.collection("InProgress");

    ItemAdapter adapter;
    FirebaseAuth mAuth;
    String Uemail;

    public DoneFragment() {

    }


    public static DoneFragment newInstance() {
        DoneFragment fragment = new DoneFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_done, container, false);
        recyclerView = v.findViewById(R.id.recycler_view_done);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth=FirebaseAuth.getInstance();
        Uemail= mAuth.getCurrentUser().getEmail();

        setUpRecyclerView(recyclerView);
        return v;
    }


    private void setUpRecyclerView(RecyclerView recyclerView) {
        Query q =DoneRef.whereEqualTo("email", Uemail);
        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(q, Item.class)
                .build();
        adapter = new ItemAdapter(options);


        recyclerView.setAdapter(adapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT|ItemTouchHelper.UP) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                int left = 4;
                if(direction==left){
                    showMessage("Przeniesiono do W Trakcie");
                    adapter.moveItem(viewHolder.getAdapterPosition(),InProgRef);
                }else{
                   showMessage("Usunięto");
                    adapter.deleteItem(viewHolder.getAdapterPosition());                }
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
    private void showMessage(String message) {
        Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
    }
}
