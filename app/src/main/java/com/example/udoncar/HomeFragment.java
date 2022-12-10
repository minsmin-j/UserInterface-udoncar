package com.example.udoncar;

import static androidx.constraintlayout.widget.StateSet.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.udoncar.model.Post;
import com.example.udoncar.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment home.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private RecyclerView mainRecyclerView;
    private MainAdapter mainAdapter;
    private RecyclerView.LayoutManager mainLayoutManager;
    private ArrayList<Post> postList;

    private Button filterBtn;
    private MainDialogActiviy dial;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User currentUser;
    private Post post;
    private TextView locaTv;

    private Bundle bundle = null;

    private List<String> region;
    private Date date;
    private  List<String> startList;
    private  List<String> destList;
    private String region2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        filterBtn = v.findViewById(R.id.mainfilter_btn);
        dial = new MainDialogActiviy((MainActivity) getActivity());

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dial.show();  //다이얼로그
            }

        });

        DocumentReference currentuserRef = db.collection("users").document(user.getEmail());
        currentuserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                String region;
//                DocumentSnapshot document = task.getResult();
//                locaTv = v.findViewById(R.id.mainloca_tv);
//                region = document.getData().get("region").toString();
//                locaTv.setText(region.substring(13,16));

                DocumentSnapshot document = task.getResult();
                region = (List<String>) document.getData().get("region");
                region2 = region.toArray()[2].toString();
                locaTv = v.findViewById(R.id.mainloca_tv);
                locaTv.setText(region2);
            }
        });

        //메인화면 리사이클러뷰
        mainRecyclerView = v.findViewById(R.id.main_rv);
        postList = new ArrayList<>();

        //dialog에서 불러오기
//        Bundle bundle = new Bundle();
//        if (bundle != null) {
//            postList = (ArrayList<Post>)bundle.getSerializable("postListD");
//
//            mainAdapter = new MainAdapter(postList, getContext());
//            mainLayoutManager = new LinearLayoutManager(getActivity());
//            mainRecyclerView.setLayoutManager(mainLayoutManager);
//            mainRecyclerView.setAdapter(mainAdapter);
//        }
        //DB에서 불러오기

//            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd HH:mm");
//            date = null;
//            try {
//                date = formatter.parse("12/12 12:12");
//            } catch (ParseException e) {
//            }
//            startList = Arrays.asList("서울특별시", "강남구", "세곡동");
//            destList = Arrays.asList("서울특별시", "강남구", "개포동");
//            post = new Post("CdLB5vxPVN", "ttttt", "ccccc", "ddddd",
//                    "택시", "일회성", "qwer@naver.com", "20대", "여자",
//                    new Date(), date, startList, destList);
//            postList.add(post);

            db.collection("users").document(user.getEmail());
            currentuserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    region = (List<String>) document.getData().get("region");
                    region2 = region.toArray()[2].toString();
                         db.collection("post")
                            .whereArrayContains("startspn", region2)
                            .orderBy("creatAt", Query.Direction.DESCENDING)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    Log.d(TAG, "onComplete", task.getException());
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "if", task.getException());
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, "for", task.getException());
                                            post = new Post();
                                            post = document.toObject(Post.class);
                                            postList.add(post);

                                            //        mainRecyclerView.setHasFixedSize(true);
                                            mainAdapter = new MainAdapter(postList, getContext());
                                            mainLayoutManager = new LinearLayoutManager(getActivity());
                                            mainRecyclerView.setLayoutManager(mainLayoutManager);
                                            mainRecyclerView.setAdapter(mainAdapter);
                                        }
                                    }
                                    else {
                                            Log.d(TAG, "else", task.getException());
                                    }
                                }
                            });
                }
            });

            Bundle bundle = getArguments();
            if (bundle != null){
                //Bundle bundle = new Bundle();
                //refresh();
                postList = (ArrayList<Post>) bundle.getSerializable("postListD");
                System.out.println("성공" + postList.size());
                mainAdapter = new MainAdapter(postList, getContext());
                mainLayoutManager = new LinearLayoutManager(getActivity());
                mainRecyclerView.setLayoutManager(mainLayoutManager);
                mainRecyclerView.setAdapter(mainAdapter);
            }



            return v;

    }
    private void refresh() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}