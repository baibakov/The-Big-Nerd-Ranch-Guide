package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecycleView;
    private CrimeAdapter mAdapter;
    private int mClickedItemPosition;

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecycleView.setAdapter(mAdapter);
        } else {
            //mAdapter.notifyDataSetChanged();
            mAdapter.notifyItemChanged(mClickedItemPosition);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecycleView = (RecyclerView) view.findViewById(R.id.crime_recycle_view);
        /**
         * Сразу же после создания виджета RecyclerView ему наначается другой объект LayoutManager. Это необходимо для работы виджета RecyclerView.
         * Если вы забудете предоставить ему объект LayoutManager, возникнет ошибка.
         * RecyclerView не занимается размещением элементов на экране самостоятельно — он поручает эту задачу LayoutManager.
         * Объект LayoutManager управляет позиционированием элементов, а также определяет поведение прокрутки.
         */
        mCrimeRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));     //LinearLayoutManager, который размещает элементы в вертикальном списке
        updateUI();
        return view;
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;
        private Crime mCrime;

        /**
         * В конструкторе CrimeHolder происходит заполнение list_item_crime.xml. Вызов на-
         * прямую передается super(…), конструктору ViewHolder. Базовый класс ViewHolder
         * хранит иерархию представлений fragment_crime_list.xml. Если понадобится эта
         * иерархия представлений, можно взять ее из поля itemView класса ViewHolder.
         * */
        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
        }

        /**
         * метод bind(Crime) будет вызываться каждый раз,
         * когда RecyclerView потребует связать заданный объект CrimeHolder с объектом
         * конкретного преступления.
         * */
        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
            mClickedItemPosition = getAdapterPosition();
            //mCrimeRecycleView.getAdapter().notifyItemMoved(0,5);
            //Toast.makeText(getActivity(), mCrime.getTitle() + " clicked", Toast.LENGTH_SHORT).show();
            /**
             * CrimeListFragment создает явный интент с указанием класса CrimeActivity.
             * CrimeListFragment использует метод getActivity() для передачи актив-
             * ности-хоста как объекта Context, необходимого конструктору Intent.
             * */
            Intent intent = CrimeActivity.newIntent(getActivity(),mCrime.getId()); //new Intent(getActivity(), CrimeActivity.class);
            /**
             * Чтобы сообщить CrimeFragment, какой объект Crime следует отображать, мож-
             * но передать идентификатор в дополнении (extra) объекта Intent при запуске
             * CrimeActivity.
             * */
            startActivity(intent);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        /**
         * Метод вызывается виджетом RecyclerView, когда ему требуется
         * новое представление для отображения элемента. В этом методе мы создаем
         * объект LayoutInflater и используем его для создания нового объекта CrimeHolder.
         * */
        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater, parent);
        }

        /**
         * Метод связывания уровня модели и объектов view.
         * Стремитесь к тому, чтобы ваша реализация onBindViewHolder(…) была как можно
         * более эффективной. В противном случае анимация прокрутки будет запинаться
         * и идти рывками, словно плохо смазанный механизм.
         * */
        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }
}
