package com.spshop.stylistpark.activity.collage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.BaseActivity;
import com.spshop.stylistpark.activity.collage.BaseFragment.LoadingListener;
import com.spshop.stylistpark.activity.collage.BaseFragment.RequestBlockingListener;
import com.spshop.stylistpark.activity.collage.BaseFragment.ShowErrDialogListener;
import com.spshop.stylistpark.activity.collage.BaseFragment.SoftKeyBoardListener;
import com.spshop.stylistpark.activity.collage.ProductDisplayFragment.OnProductClickListener;
import com.spshop.stylistpark.activity.collage.ProductDisplayFragment.OnProductListSlideListener;
import com.spshop.stylistpark.adapter.CollageProductListAdapter.DisplayMode;
import com.spshop.stylistpark.entity.Product;
import com.spshop.stylistpark.utils.BitmapUtil;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class GeneratorSelectProductActivity extends BaseActivity implements
		OnProductClickListener, SoftKeyBoardListener, OnProductListSlideListener {
	
    private static final String TAG = "GeneratorSelectProductActivity";
    private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
    
    private ImageView iv_photo;
    private TextView tv_collage_items;
    private RecyclerView v_recycler;
    private ProductMainFragment fg_product_main;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    
    private Uri imgUri;
    private DisplayImageOptions options;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator_product);
        
        imgUri = getIntent().getParcelableExtra(AppConfig.ACTIVITY_KEY_COLLAGE_URI);
        options = AppApplication.getDefaultImageOptions();
        
        findViewById();
		initView();
    }

	private void findViewById() {
		iv_photo = (ImageView) findViewById(R.id.select_product_iv_photo);
		tv_collage_items = (TextView) findViewById(R.id.select_product_tv_collage_items);
		v_recycler = (RecyclerView) findViewById(R.id.select_product_view_recycler);
		fg_product_main = (ProductMainFragment) getSupportFragmentManager().findFragmentById(R.id.select_product_fg_product_main);
	}

	private void initView() {
		setTitle(R.string.collage_select_product);
		setBtnRight(getString(R.string.confirm));
		// 初始化选择的相片
        int previewSide = getResources().getDimensionPixelSize(R.dimen.collage_preview_side);
        Bitmap bitmap = BitmapUtil.getBitmap(imgUri.getPath(), previewSide, previewSide);
        iv_photo.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
        
        initRecycler();
        initFragment();
	}

	/**
	 * 初始化RecyclerView
	 */
	private void initRecycler() {
		v_recycler.addItemDecoration(new SimpleDividerItemDecoration(this));
        v_recycler.setHasFixedSize(true);
        v_recycler.setLayoutManager(new GridLayoutManager(this, 2));
        
		productList = new ArrayList<Product>();
        productAdapter = new ProductAdapter(this, productList);
        productAdapter.setListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	productList.remove(position);
                if(productList.isEmpty()){
                	tv_collage_items.setVisibility(View.GONE);
                }
                productAdapter.notifyDataSetChanged();
            }
            
        });
        v_recycler.setAdapter(productAdapter);
	}

	/**
	 * 初始化商品Fragment
	 */
	private void initFragment() {
		fg_product_main.setIncludeDecoration(false);
        fg_product_main.setOnProductListSlideListener(this);
        fg_product_main.setOnProductClickListener(this);

        fg_product_main.setShowErrDialogListener(new ShowErrDialogListener(){

            @Override
            public void showErrDialog(String msg) {
                showErrorDialog(msg);
            }
        });
        fg_product_main.setLoadingListener(new LoadingListener(){

            @Override
            public void onHideLoading() {
                  LogUtil.i(TAG,"event-onHideLoading()");
                  stopAnimation();
            }

            @Override
            public void onShowLoading() {
                  LogUtil.i(TAG,"event-onShowLoading()");
                  startAnimation();
            }
        });
        fg_product_main.setRequestBlockingListener(new RequestBlockingListener(){

            @Override
            public void onReleaseBlock() {
                LogUtil.i(TAG,"event-onReleaseBlock()");
//                setCanClickIntecptLayout(true);
//                showInterceotLayout(false); //Xu
            }

            @Override
            public void onRequestBlock() {
                 LogUtil.i(TAG,"event-onRequestBlock()");
//                 setCanClickIntecptLayout(false);
//                 showInterceotLayout(true); //Xu
            }
        });
	}

    
    @Override
    public void onBackPressed() { //物理返回键
        if (!fg_product_main.onBackPressed()) {
            ask4Leave();
        }else {
        	fg_product_main.onKeyBoardShow(false);
		}
    }
    
	private void ask4Leave() {
        showConfirmDialog(R.string.collage_msg_leave_confirm, getString(R.string.confirm),
                getString(R.string.cancel), true, true, new Handler(){

                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case DIALOG_CANCEL_CLICK:
                                finish();
                                break;
                        }
                    }

                });
    }

    @Override
    public void onShowSoftKeyBoard() {
    	fg_product_main.onKeyBoardShow(true);
    }

    @Override
    public void onHideSoftKeyBoard() {
    	fg_product_main.onKeyBoardShow(false);
    }

    @Override
    public void onProductClick(Product product, DisplayMode mode, String productCats) {
    	productList.add(product);
        tv_collage_items.setVisibility(View.VISIBLE);
        productAdapter.notifyDataSetChanged();
    }
    
    @Override
    public void OnListenerLeft() {
    	ask4Leave();
    }
    
    @Override
    public void OnListenerRight() {
    	super.OnListenerRight();
    	
    	if(!productList.isEmpty()){
            Intent intent = new Intent(GeneratorSelectProductActivity.this, GeneratorPhotoEditActivity.class);
            intent.putExtra(GeneratorPhotoEditActivity.COLLAGE_URI, imgUri);
            intent.putExtra(GeneratorPhotoEditActivity.COLLAGE_LIST, getProductIds());
            startActivity(intent);
        }else{
        	showErrorDialog(R.string.collage_no_item);
        }
    }
    
    private String[] getProductIds(){
        String[] productIdList = new String[productList.size()];
        int j = 0;
        for (int i = 0; i < productList.size(); i++)
        {
            productIdList[j] = productList.get(i).getItemId();
            j++;
        }
        return productIdList;
    } 
    
    @Override
    public void onSlideUpEnd() {
    	setBtnLeftGone(View.GONE);
    	setBtnRightGone(View.GONE);
    }

    @Override
    public void onSlideDownEnd() {
    	setBtnLeftGone(View.VISIBLE);
    	setBtnRightGone(View.VISIBLE);
    }
    
    private class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {

        private List<Product> datas;
        private String dollarSign;
        private OnItemClickListener listener;
        
        public ProductAdapter(Context context, List<Product> productList) {
            this.datas = productList;
            dollarSign = LangCurrTools.getCurrencyValue(mContext) + "";
        }
        
        @Override
        public int getItemCount() {
            return datas.size();
        }

        @Override
        public void onBindViewHolder(ProductViewHolder vh, int i) {
            Product product = datas.get(i);
            ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + product.getThumbUrl(), vh.ivProduct, options);
            vh.tvName.setText(product.getBrand());
            vh.tvDesc.setText(product.getName());
            vh.tvPrice.setText(dollarSign + product.getPrice());
            vh.ivClose.setTag(String.valueOf(i));
            vh.ivClose.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    String iStr= (String) v.getTag();
                    int i = Integer.parseInt(iStr);
                    listener.onItemClick(null, null, i, i);
                }
            });
        }

        @Override
        public ProductViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_select_product, null);
            ProductViewHolder vh = new ProductViewHolder(v);
            return vh;
        }

        public void setListener(OnItemClickListener listener) {
            this.listener = listener;
        }
        
    }
    
    private class ProductViewHolder extends RecyclerView.ViewHolder {
        protected ImageView ivProduct;
        protected ImageView ivClose;
        protected TextView tvName, tvDesc, tvPrice;

        public ProductViewHolder(View view) {
            super(view);
            this.ivProduct = (ImageView) view.findViewById(R.id.ivProduct);
            this.ivClose = (ImageView) view.findViewById(R.id.ivClose);
            this.tvName = (TextView) view.findViewById(R.id.tvName);
            this.tvDesc = (TextView) view.findViewById(R.id.tvDesc);
            this.tvPrice = (TextView) view.findViewById(R.id.tvPrice);
        }
    }
    
    private class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;
     
        public SimpleDividerItemDecoration(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.shape_line_divider_gray);
        }
     
        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
     
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);
     
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
     
                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();
     
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
                
                if(i%2 == 0) {
                    mDivider.setBounds(child.getRight() - mDivider.getIntrinsicWidth(), child.getTop(), child.getRight(), child.getBottom());
                    mDivider.draw(c);
                }
            }
        }
    }
    
}
