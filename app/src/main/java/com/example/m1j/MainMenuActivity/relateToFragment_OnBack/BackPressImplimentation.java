package com.example.m1j.MainMenuActivity.relateToFragment_OnBack;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


//Во всех трех классах, данной папке идет отработка методов при перемещении назад по фрагментам.
//Так как, нам потребуется держать сессию, да еще и к тому что мы иногда храним фрагменты в стеке, а не уничтожаем при смене на другой
//То нам требуется отслеживать те фрагменты, на которые мы перемещаемся. Смотреть наличие дочерних элементов и менять их стек.
//Все три класса, в этой папке как раз этим и занимаются. Их можно было засунуть в один, но я решил обосбить
public class BackPressImplimentation implements OnBackPressListener {

    private Fragment parentFragment;

    public BackPressImplimentation(Fragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    @Override
    public boolean onBackPressed() {

        if (parentFragment == null) return false;

        int childCount = parentFragment.getChildFragmentManager().getBackStackEntryCount();

        if (childCount == 0) {
            return false;
        } else {
            FragmentManager childFragmentManager = parentFragment.getChildFragmentManager();
            OnBackPressListener childFragment = (OnBackPressListener) childFragmentManager.getFragments().get(0);


            if (!childFragment.onBackPressed()) {
                childFragmentManager.popBackStackImmediate();

            }

            return true;
        }
    }
}