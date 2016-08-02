package com.ruddell.museumofthebible.Exhibits;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ruddell.museumofthebible.BaseActivity.BaseActivity;
import com.ruddell.museumofthebible.R;

public class ExhibitDetailActivity extends BaseActivity {
    private boolean mIsFavorited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibit_detail);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("Biblical Codices");
        //Uncial Manuscripts

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp, null));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed()
                ExhibitDetailActivity.this.finish();
            }
        });

        toolbar.inflateMenu(R.menu.exhibit_detail_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                mIsFavorited = !mIsFavorited;
                item.setIcon(getResources().getDrawable(mIsFavorited ? R.drawable.ic_turned_in_white_24dp : R.drawable.ic_turned_in_not_white_24dp, getTheme()));
                return false;
            }
        });



    }

}
