package com.bhanwar.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.bhanwar.android.R;
import com.bhanwar.android.adapter.ArticleListAdapter;
import com.bhanwar.android.db.DbAdapter;
import com.bhanwar.android.rss.domain.Article;

public class ArticleListActivity extends FragmentActivity implements ArticleListFragment.Callbacks
{

	private boolean mTwoPane;
	private DbAdapter dba;

	public ArticleListActivity()
	{
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_article_list);
		dba = new DbAdapter(this);

		if (findViewById(R.id.article_detail_container) != null)
		{
			mTwoPane = true;
			((ArticleListFragment) getSupportFragmentManager().findFragmentById(R.id.article_list)).setActivateOnItemClick(true);
		}
	}

	@Override
	public void onItemSelected(String id)
	{
		Article selected = (Article) ((ArticleListFragment) getSupportFragmentManager().findFragmentById(R.id.article_list)).getListAdapter()
				.getItem(Integer.parseInt(id));

		// mark article as read
		dba.openToWrite();
		dba.markAsRead(selected.getGuid());
		dba.close();
		selected.setRead(true);
		ArticleListAdapter adapter = (ArticleListAdapter) ((ArticleListFragment) getSupportFragmentManager().findFragmentById(R.id.article_list))
				.getListAdapter();
		adapter.notifyDataSetChanged();
		Log.e("CHANGE", "Changing to read: ");

		// load article details to main panel
		Bundle arguments = new Bundle();
		arguments.putSerializable(Article.KEY, selected);

		if (mTwoPane)
		{
			ArticleDetailFragment fragment = new ArticleDetailFragment();

			fragment.setArguments(arguments);

			getSupportFragmentManager().beginTransaction().replace(R.id.article_detail_container, fragment).commit();

		}
		else
		{
			Intent detailIntent = new Intent(this, ArticleDetailActivity.class);

			detailIntent.putExtra("arguments", arguments);

			startActivity(detailIntent);
		}
	}
}
