/**
 * Programmers {
 *  Mario Aguilera Piceno
 * }
 * Course: CS 460 - Software Engineering for Mobile Devices
 * Original Implementation: Professor Shangyue Zhu
 * Honor Code: I pledge that this work (aside from Professor Zhu's base implementation), is my work and that I have not received nor given help to anyone else.
 * Date: 10/19/2026
 * Modified: 10/24/2026
 * File: MainActivity.java
 * About: Create
 */

// Package declaration
package com.example.poster1019;

// Required imports
import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

/**
 * Main Class
 */
public class MainActivity extends AppCompatActivity {

    private static final String KEY_CURRENT_PAGE = "current_page";

    //Tags to find each page's Fragment again in the FragmentManager.
    private static final String TAG_PAGE_MAIN = "page_main";
    private static final String TAG_PAGE_FAVORITES = "page_favorites";
    private static final String TAG_PAGE_CONTACT = "page_contact";
    private static final String TAG_PAGE_MORE = "page_more";

    //drawer the side menu inside it, and the bottom bar.
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigation;

    private int currentPageId = R.id.nav_main;

    private PosterRepository posterRepository;
    private final PosterRepository.FavoritesListener favoritesBadgeListener = this::updateFavoritesBadge;

    /**
     * Updates the favorites amount display.
     */
    private void updateFavoritesBadge() {

        int count = posterRepository.getFavoriteCount();
        BadgeDrawable badge = bottomNavigation.getOrCreateBadge(R.id.nav_favorites);
        badge.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBadge));
        badge.setBadgeTextColor(ContextCompat.getColor(this, R.color.colorTextIcons));
        badge.setNumber(count);
        badge.setVisible(count > 0);
    }

    /**
     * App creation entry point.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //find by id
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //The drawer and the bottom bar both just call showPage().
        // Returning true tells them to show the tapped item as selected.
        navigationView.setNavigationItemSelectedListener(item -> {
            showPage(item.getItemId());
            return true;
        });
        bottomNavigation.setOnItemSelectedListener(item -> {
            showPage(item.getItemId());
            return true;
        });

        //Window insets: the sizes of the status bar, navigation bar and camera cut-out.
        final AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        final View fragmentContainer = findViewById(R.id.fragmentContainer);

        final View drawerHeader = navigationView.getHeaderView(0);
        final int drawerHeaderPaddingTop = drawerHeader.getPaddingTop();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            appBarLayout.setPadding(bars.left, bars.top, bars.right, 0);
            fragmentContainer.setPadding(bars.left, 0, bars.right, 0);
            drawerHeader.setPadding(drawerHeader.getPaddingLeft(), drawerHeaderPaddingTop + bars.top,
                    drawerHeader.getPaddingRight(), drawerHeader.getPaddingBottom());
            return insets;
        });

        // Adds all pages to FragmentManager: main, favorites, contact, and more.
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, new PostersFragment(), TAG_PAGE_MAIN)
                    .add(R.id.fragmentContainer, new FavoritesFragment(), TAG_PAGE_FAVORITES)
                    .add(R.id.fragmentContainer, new ContactFragment(), TAG_PAGE_CONTACT)
                    .add(R.id.fragmentContainer, new MoreFragment(), TAG_PAGE_MORE)
                    .commitNow();
        } else {
            currentPageId = savedInstanceState.getInt(KEY_CURRENT_PAGE, R.id.nav_main);
        }

        // Shows the current page
        showPage(currentPageId);

        //Favorites badge: show the current count now, then follow every change.
        posterRepository = PosterRepository.getInstance(this);
        posterRepository.addFavoritesListener(favoritesBadgeListener);
        updateFavoritesBadge();
    }

    /**
     * Removes active listener for memory safety.
     */
    @Override
    protected void onDestroy() {
        posterRepository.removeFavoritesListener(favoritesBadgeListener);
        super.onDestroy();
    }

    /**
     * Remember the current page, so a rotation doesn't jump back to the Main Page.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_PAGE, currentPageId);
    }

    /**
     * Shows a page depending on the page id.
     *
     * @param pageId the TAG of the page being displayed.
     */
    private void showPage(int pageId) {
        currentPageId = pageId;

        //Show this page's Fragment and hide the other two. show/hide (instead of
        // replace) keeps each page alive, with its scroll position and selections.
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        String visibleTag = tagForPage(pageId);
        for (String tag : new String[]{TAG_PAGE_MAIN, TAG_PAGE_FAVORITES, TAG_PAGE_CONTACT, TAG_PAGE_MORE}) {
            Fragment page = fragmentManager.findFragmentByTag(tag);
            if (page == null) {
                continue;
            }
            if (tag.equals(visibleTag)) {
                transaction.show(page);
            } else {
                transaction.hide(page);
            }
        }
        transaction.commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleForPage(pageId));
        }

        navigationView.setCheckedItem(pageId);
        bottomNavigation.getMenu().findItem(pageId).setChecked(true);

        drawerLayout.closeDrawer(GravityCompat.START);

    }

    /**
     * Toolbar title for each page.
     *
     * @param pageId the TAG of the page being altered.
     * @return Returns the string representation of the page title.
     */
    private int titleForPage(int pageId) {
        if (pageId == R.id.nav_favorites) {
            return R.string.title_favorites;
        } else if (pageId == R.id.nav_contact) {
            return R.string.title_contact;
        } else if (pageId == R.id.nav_more) {
            return R.string.title_more;
        }
        return R.string.posters;
    }

    /**
     * Which Fragment tag belongs to a page.
     *
     * @param pageId the id of the TAG being looked for.
     * @return Returns the tag of the page being looked for.
     */
    private String tagForPage(int pageId) {
        if (pageId == R.id.nav_favorites) {
            return TAG_PAGE_FAVORITES;
        } else if (pageId == R.id.nav_contact) {
            return TAG_PAGE_CONTACT;
        } else if (pageId == R.id.nav_more) {
            return TAG_PAGE_MORE;
        }
        return TAG_PAGE_MAIN;
    }
}