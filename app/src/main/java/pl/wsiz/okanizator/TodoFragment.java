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


public class TodoFragment extends Fragment {

    private View v;
    private RecyclerView recyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference TodoRef = db.collection("ToDo");
    CollectionReference InProgRef = db.collection("InProgress");

    ItemAdapter adapter;
    FirebaseAuth mAuth;
    String Uemail;

    public TodoFragment() {

    }


    public static TodoFragment newInstance() {
        TodoFragment fragment = new TodoFragment();
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
        v=inflater.inflate(R.layout.fragment_todo, container, false);
       recyclerView = v.findViewById(R.id.recycler_view_todo);
        recyclerView.setHasFixedSize(true);

    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth=FirebaseAuth.getInstance();
        Uemail= mAuth.getCurrentUser().getEmail();

        setUpRecyclerView(recyclerView);
        return v;
    }


    private void setUpRecyclerView(RecyclerView recyclerView) {
        Query q =TodoRef.whereEqualTo("email", Uemail);
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


                int right = 8;
                if(direction==right){
                    showMessage( " Przeniesiono do  W Trakcie ");
                    adapter.moveItem(viewHolder.getAdapterPosition(),InProgRef);
                }else{
                    showMessage(" Usunięto ");
                    adapter.deleteItem(viewHolder.getAdapterPosition());
                }

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

////////////////////////// **********************stara wersja z wbudowanym adapterem*************************************************
//    @Override
//    public void onStart() {
//        super.onStart();
//        Query q =TodoRef.whereEqualTo("email", Uemail);
//        FirestoreRecyclerOptions options=new FirestoreRecyclerOptions.Builder<Item>().setQuery(q, Item.class).build();
//        FirestoreRecyclerAdapter<Item, ItemViewHolder>adapter= new FirestoreRecyclerAdapter<Item, ItemViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i, @NonNull Item item) {
//                itemViewHolder.textViewItem.setText(item.getTextitem());
//
//
//            }
//
//
//            @NonNull
//            @Override
//            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
//                ItemViewHolder viewHolder = new ItemViewHolder(v);
//                return viewHolder;
//            }
//            public void deleteItem(int position) {
//                getSnapshots().getSnapshot(position).getReference().delete();
//            }
//        };
//        recyclerView.setAdapter(adapter);
//        adapter.startListening();
//
//
//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
//                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return false;
//            }
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                int documentId = viewHolder.getAdapterPosition();
//                Log.e("tag", String.valueOf(documentId));
//                adapter.dele(documentId);
//            }
//        }).attachToRecyclerView(recyclerView);
//
//        }
//
//
//public static class ItemViewHolder extends RecyclerView.ViewHolder{
//
//        TextView textViewItem;
//
//
//
//    public ItemViewHolder(@NonNull View itemView) {
//        super(itemView);
//       textViewItem = itemView.findViewById(R.id.item_text);
//
//    }
//}
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        v=inflater.inflate(R.layout.fragment_todo, container, false);
//       recyclerView = v.findViewById(R.id.recycler_view_todo);
//    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        mAuth=FirebaseAuth.getInstance();
//        Uemail= mAuth.getCurrentUser().getEmail();
//
//
//        return v;
//    }
//
////    private void deleteItem(int documentId) {
////        Query q =TodoRef.whereEqualTo("id", Uemail);
////    }
//
//
//}