package com.example.hp.neveralone.Fragments;

        import android.os.Bundle;
        import android.support.annotation.NonNull;
        import android.support.v4.app.Fragment;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import com.example.hp.neveralone.Adapter.UserAdapter;
        import com.example.hp.neveralone.Model.Chat;
        import com.example.hp.neveralone.Model.User;
        import com.example.hp.neveralone.Notifications.Token;
        import com.example.hp.neveralone.R;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.google.firebase.iid.FirebaseInstanceId;

        import java.util.ArrayList;
        import java.util.Iterator;
        import java.util.List;


public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<User> mUsers;

    FirebaseUser fuser;
    DatabaseReference reference;

    private  List<String> usersList;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats,container,false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getSender().equals(fuser.getUid())) {
                        usersList.add(chat.getReciever());
                    }

                    if (chat.getReciever().equals(fuser.getUid())) {
                        usersList.add(chat.getSender());
                    }

                }

                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return view;
    }


    private  void readChats() {
        mUsers = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);



                    for(String id :usersList){
                        if(user.getId().equals(id)){
                            if (mUsers.size() != 0){
                                for (User user1 : mUsers ){
                                    if(user.getId()!=null  && !user.getId().equals(user1.getId())){
                                        mUsers.add(user);
                                    }
                                }
                            } else {
                                mUsers.add(user);
                            }
                        }
                    }


//                    for(String id :usersList){
//                        if(user.getId()!=null && user.getId().equals(id)){
//                            if (mUsers.size() != 0){
//                                for (Iterator<User> iterator = mUsers.iterator(); iterator.hasNext(); ){
//                                    if(user.getId()!=null ){
//                                        User u =iterator.next();
//                                        if(!user.getId().equals(u.getId())) {
//                                            mUsers.add(u);
//                                            iterator.remove();
//                                        }
//                                    }
//                                }
//                            } else {
//                                mUsers.add(user);
//                            }
//                        }
//                    }


//                    for(String id :usersList){
//                        if(user.getId()!=null && user.getId().equals(id)){
//                            if (mUsers.size() != 0){
//                                for (Iterator<User> iterator = mUsers.iterator(); iterator.hasNext(); ){
//                                    if(user.getId()!=null ){
//                                        User u =iterator.next();
//                                        iterator.remove();
//                                        if(!user.getId().equals(u.getId())) {
//                                            mUsers.add(u);
//                                        }
//                                    }
//                                }
//                            } else {
//                                mUsers.add(user);
//                            }
//                        }
//                    }
                }

                userAdapter = new UserAdapter(getContext(),mUsers,true);
                recyclerView.setAdapter(userAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateToken(FirebaseInstanceId.getInstance().getToken());
    }


    private void updateToken (String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);

        reference.child(fuser.getUid()).setValue(token1);
    }


}