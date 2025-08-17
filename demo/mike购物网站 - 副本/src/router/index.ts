import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/Home.vue';
import ProductDetailView from '../views/ProductDetail.vue';
import LoginView from '../views/Login.vue';
import RegisterView from '../views/Register.vue';
import ProfileView from '../views/Profile.vue';
import MemberCenter from '../views/MemberCenter.vue';
import EditProfileView from '../views/EditProfile.vue';
import MikeShopView from '../views/mikeShop.vue';
import CartView from '../views/Cart.vue';
import FavoritesView from '../views/Favorites.vue';
import CheckoutView from '../views/Checkout.vue';
import OrderSuccessView from '../views/OrderSuccess.vue';
import PaymentView from '../views/Payment.vue';
import PaymentSuccessView from '../views/PaymentSuccess.vue';
import OrdersView from '../views/Orders.vue';
import AdminLoginView from '../views/admin/AdminLogin.vue';
import AdminDashboardView from '../views/admin/AdminDashboard.vue';
import ProductManagementView from '../views/admin/ProductManagement.vue';
import OrderManagementView from '../views/admin/OrderManagement.vue';
import UserManagementView from '../views/admin/UserManagement.vue';
import CategoryManagementView from '../views/admin/CategoryManagement.vue';
import WalletView from '../views/Wallet.vue';
import PaymentMethodsView from '../views/PaymentMethods.vue';
import OrderDetailView from '../views/OrderDetail.vue';
import ImageManagementView from '../views/admin/ImageManagement.vue';
import CustomerServiceView from '../views/admin/CustomerService.vue';
import CustomerServicePageView from '../views/CustomerService.vue';
import ChatRoomView from '../views/ChatRoom.vue';


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL || '/'),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView
    },
    {
      path: '/profile',
      name: 'profile',
      component: ProfileView,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/member-center',
      name: 'member-center',
      component: MemberCenter,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/profile/edit',
      name: 'editProfile',
      component: EditProfileView,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/addresses',
      name: 'addressManagement',
      component: () => import('../views/AddressManagement.vue'),
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/wallet',
      name: 'wallet',
      component: WalletView,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/payment-methods',
      name: 'paymentMethods',
      component: PaymentMethodsView,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/test-avatar',
      name: 'test-avatar',
      component: () => import('../views/TestAvatar.vue')
    },
    {
      path: '/mikeshop',
      name: 'mikeshop',
      component: MikeShopView
    },
    {
      path: '/product/:id',
      name: 'productDetail',
      component: ProductDetailView
    },
    {
      path: '/cart',
      name: 'cart',
      component: CartView
    },
    {
      path: '/favorites',
      name: 'favorites',
      component: FavoritesView
    },
    {
      path: '/checkout',
      name: 'checkout',
      component: CheckoutView,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/order-success',
      name: 'orderSuccess',
      component: OrderSuccessView,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/payment',
      name: 'payment',
      component: PaymentView,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/payment-success',
      name: 'paymentSuccess',
      component: PaymentSuccessView,
      meta: { requiresAuth: true }
    },
    {
      path: '/orders',
      name: 'orders',
      component: OrdersView,
      meta: { requiresAuth: true }
    },
    {
      path: '/orders/:id',
      name: 'orderDetail',
      component: OrderDetailView,
      meta: { requiresAuth: true }
    },
    { path: '/customer-service', name: 'customerService', component: CustomerServicePageView },
    { path: '/chat-room', name: 'chatRoom', component: ChatRoomView },
    {
      path: '/admin/login',
      name: 'adminLogin',
      component: AdminLoginView
    },
    {
      path: '/admin',
      name: 'adminDashboard',
      component: AdminDashboardView,
      meta: {
        requiresAdminAuth: true
      },
      children: [
        {
          path: 'product',
          name: 'productManagement',
          component: ProductManagementView
        },
        {
          path: 'product/add',
          name: 'addProduct',
          component: () => import('../views/admin/ProductManagement.vue')
        },
        {
          path: 'product/edit/:id',
          name: 'editProduct',
          component: () => import('../views/admin/ProductManagement.vue')
        },
        {
          path: 'product/detail/:id',
          name: 'productDetailManagement',
          component: () => import('../views/admin/ProductManagement.vue')
        },
        {
          path: 'orders',
          name: 'orderManagement',
          component: OrderManagementView
        },
        {
          path: 'users',
          name: 'userManagement',
          component: UserManagementView
        },
        {
          path: 'categories',
          name: 'categoryManagement',
          component: CategoryManagementView
        },
        {
          path: 'images',
          name: 'imageManagement',
          component: ImageManagementView
        },
        {
          path: 'customer-service',
          name: 'adminCustomerService',
          component: CustomerServiceView
        }
      ]
    },

  ]
});

// 添加路由守卫以验证JWT令牌
router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const requiresAdminAuth = to.matched.some(record => record.meta.requiresAdminAuth);
  const token = localStorage.getItem('token');
  const adminToken = localStorage.getItem('adminToken');

  if (requiresAdminAuth && !adminToken) {
    next('/admin/login');
  } else if (requiresAuth && !token) {
    next('/login');
  } else {
    next();
  }
});

export default router;
