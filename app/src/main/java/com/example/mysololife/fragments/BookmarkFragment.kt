package com.example.mysololife.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mysololife.R
import com.example.mysololife.contentsList.BookmarkRVAdapter
import com.example.mysololife.contentsList.ContentModel
import com.example.mysololife.databinding.FragmentBookmarkBinding
import com.example.mysololife.databinding.FragmentTalkBinding
import com.example.mysololife.utils.FBAuth
import com.example.mysololife.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class BookmarkFragment : Fragment() {

    private lateinit var binding : FragmentBookmarkBinding

    private val TAG = BookmarkFragment::class.java.simpleName

    val items = ArrayList<ContentModel>()
    val itemKeyList = ArrayList<String>()
    val bookmarkIdList = mutableListOf<String>()

    lateinit var rvAdapter: BookmarkRVAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bookmark, container, false)


        // 2. 사용자가 북마크한 정보를 다 가져온다.
        getBookmarkData()


        rvAdapter = BookmarkRVAdapter(requireContext(), items, itemKeyList, bookmarkIdList)

        val rv : RecyclerView = binding.bookmarkRV
        rv.adapter = rvAdapter

        rv.layoutManager = GridLayoutManager(requireContext(), 2)



        binding.tipTap.setOnClickListener{
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_tipFragment)
        }

        binding.storeTap.setOnClickListener{
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_storeFragment)
        }

        binding.talkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_talkFragment)
        }

        binding.homeTap.setOnClickListener{
            it.findNavController().navigate(R.id.action_bookmarkFragment_to_homeFragment)
        }

        return binding.root
    }

    private fun getCategoryData(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for(dataModel in dataSnapshot.children){
                    Log.d(TAG, dataModel.toString())
                    val item = dataModel.getValue(ContentModel::class.java)

                    // 3. 전체 컨텐츠 중에서, 사용자가 북마크한 정보만 보여준다.
                    if(bookmarkIdList.contains(dataModel.key.toString())){
                        items.add(item!!)
                        itemKeyList.add(dataModel.key.toString())
                    }

                }

                rvAdapter.notifyDataSetChanged()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.category1.addValueEventListener(postListener)
        FBRef.category2.addValueEventListener(postListener)
    }

    private fun getBookmarkData(){
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for(dataModel in dataSnapshot.children){

                    Log.e(TAG, dataModel.toString())
                    bookmarkIdList.add(dataModel.key.toString())
                }

                // 1. 전체 카테고리에 있는 컨텐츠 데이터들을 다 가져온다.
                getCategoryData()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("ContentListActivity", "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.bookmarkRef.child(FBAuth.getUid()).addValueEventListener(postListener)
    }
}