package com.example.healthyplus.Activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.healthyplus.Adapter.ProductAdapter;
import com.example.healthyplus.Model.Product;
import com.example.healthyplus.Model.User;
import com.example.healthyplus.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private CardView userCardView, controlCaloriesCardView, productCardView, technologyCardView,
            controlWaterCardView, dishCardView, exerciseCardView, suggestionCardView, ingredientCardView, bill;
    ImageView imvCart;
    List<Product> list = new ArrayList<>();
    FirebaseFirestore db;
    TextView hello;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        hello = findViewById(R.id.txvHello);
        imvCart = findViewById(R.id.img_cart);
        ingredientCardView = findViewById(R.id.cvIngre);
        recyclerView=findViewById(R.id.rec);
        userCardView=findViewById(R.id.cv_user);
        controlCaloriesCardView=findViewById(R.id.cv_control_calories);
        productCardView=findViewById(R.id.cv_product);
        technologyCardView=findViewById(R.id.cv_technology_product);
        controlWaterCardView=findViewById(R.id.cv_control_water);
        dishCardView=findViewById(R.id.cv_dish);
        exerciseCardView=findViewById(R.id.cv_exercise);
        suggestionCardView = findViewById(R.id.cv_suggestion);
        bill = findViewById(R.id.cv_bill);

        productAdapter=new ProductAdapter(this);
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        productAdapter.setData(list);
        recyclerView.setAdapter(productAdapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        db = FirebaseFirestore.getInstance();
        db.collection("product").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product p = document.toObject(Product.class);
                                list.add(p);
                                productAdapter.notifyDataSetChanged();
                                Log.e(TAG, document.getId() + " => " + document.getData() + p.getImg());
                            }
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("user").document(u.getUid()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    User user = task.getResult().toObject(User.class);
                                    hello.setText(user.getName());
                                }
                            }
                        });
        imvCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
            }
        });
        ingredientCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), IngredientActivity.class);
                startActivity(intent);
            }
        });
        suggestionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), SuggestionActivity.class);
                startActivity(intent);
            }
        });
        exerciseCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), ExerciseTimerActivity.class);
                startActivity(intent);
            }
        });
        userCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                startActivity(intent);
            }
        });
        controlCaloriesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ControlCaloriesActivity.class);
                startActivity(intent);
            }
        });
        productCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProductActivity.class);
                startActivity(intent);
            }
        });
        technologyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TechnologyProductActivity.class);
                startActivity(intent);
            }
        });
        controlWaterCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), ControlWaterActivity.class);
                startActivity(intent);
            }
        });

        dishCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), DishActivity.class);
                startActivity(intent);
            }
        });
        bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), BillActivity.class);
                startActivity(intent);
            }
        });




    }
    private List<Product> getListProduct()
    {
        List<Product> list = new ArrayList<>();
        /*list.add(new Product(1, "Viên uống dầu cá Omega3", 570000, "gs://healthyplus-612a6.appspot.com/food_products/sp_01.jpg", "Healthy Care", 1));
        list.add(new Product(2, "Bánh biscostti vị matcha", 50000, "gs://healthyplus-612a6.appspot.com/food_products/sp_02.jpg", "Rainbow Healthy", 1));
        list.add(new Product(3, "Hạt chia hữu cơ chuẩn Organic", 70000, "gs://healthyplus-612a6.appspot.com/food_products/sp_03.jpg", "Rainbow Healthy", 1));*/
        return list;
    }

}