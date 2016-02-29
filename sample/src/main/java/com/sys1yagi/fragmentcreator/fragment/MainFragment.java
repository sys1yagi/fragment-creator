package com.sys1yagi.fragmentcreator.fragment;

import com.sys1yagi.fragmentcreator.R;
import com.sys1yagi.fragmentcreator.annotation.FragmentCreator;
import com.sys1yagi.fragmentcreator.model.Product;
import com.sys1yagi.fragmentcreator.model.Shop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

@FragmentCreator
public class MainFragment extends Fragment {

    final static String REQUIRED_ARGUMENTS = "Required Arguments";

    final static String OPTIONAL_ARGUMENTS = "Optional Arguments";

    final static String NO_ARGUMENTS = "No Arguments";

    final static String DEFAULT_VALUES = "Default Values";

    final static String ARGUMENTS_VALIDATOR = "Arguments Validator";

    final static String TYPE_SERIALIZER = "Type Serializer";

    final static String INHERITANCE = "Inheritance";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainFragmentCreator.read(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = (ListView) view.findViewById(R.id.list_view);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        adapter.add(REQUIRED_ARGUMENTS);
        adapter.add(OPTIONAL_ARGUMENTS);
        adapter.add(NO_ARGUMENTS);
        adapter.add(DEFAULT_VALUES);
        adapter.add(ARGUMENTS_VALIDATOR);
        adapter.add(TYPE_SERIALIZER);
        adapter.add(INHERITANCE);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = adapter.getItem(position);
                openFragment(title);
            }
        });
    }

    void openFragment(String title) {
        switch (title) {
            case REQUIRED_ARGUMENTS:
                openRequiredArgumentsFragment();
                break;
            case OPTIONAL_ARGUMENTS:
                openOptionalArgumentsFragment();
                break;
            case NO_ARGUMENTS:
                openNoArgumentsFragment();
                break;
            case DEFAULT_VALUES:
                openDefaultValueArgumentsFragment();
                break;
            case TYPE_SERIALIZER:
                openTypeSerializerFragment();
                break;
            default:
                Toast.makeText(getContext(), "Not Yet Implemented...", Toast.LENGTH_SHORT).show();
        }
    }

    void openRequiredArgumentsFragment() {
        getFragmentManager()
                .beginTransaction()
                .addToBackStack(REQUIRED_ARGUMENTS)
                .replace(
                        R.id.container,
                        RequiredArgumentsFragmentCreator
                                .newBuilder("Hello Fragment Creator", new Shop(10, "Fragment Creator Store"))
                                .build())
                .commit();
    }

    void openOptionalArgumentsFragment() {
        List<Shop> shops = new ArrayList<>();
        shops.add(new Shop());
        shops.add(new Shop());
        shops.add(new Shop());

        getFragmentManager()
                .beginTransaction()
                .addToBackStack(OPTIONAL_ARGUMENTS)
                .replace(
                        R.id.container,
                        OptionalArgumentsFragmentCreator
                                .newBuilder()
                                .setId(10)
                                .setKeyword("test")
                                .setShops(shops)
                                .build())
                .commit();
    }

    void openNoArgumentsFragment() {
        getFragmentManager()
                .beginTransaction()
                .addToBackStack(NO_ARGUMENTS)
                .replace(
                        R.id.container,
                        NoArgumentsFragmentCreator
                                .newBuilder()
                                .build())
                .commit();
    }

    void openDefaultValueArgumentsFragment() {
        getFragmentManager()
                .beginTransaction()
                .addToBackStack(DEFAULT_VALUES)
                .replace(
                        R.id.container,
                        DefaultValueFragmentCreator
                                .newBuilder()
                                .build())
                .commit();
    }

    void openTypeSerializerFragment() {
        Product product = new Product();
        product.setId(10);
        product.setName("product");
        getFragmentManager()
                .beginTransaction()
                .addToBackStack(TYPE_SERIALIZER)
                .replace(R.id.container,
                        TypeSerializerFragmentCreator.newBuilder(product).build())
                .commit();
    }
}
