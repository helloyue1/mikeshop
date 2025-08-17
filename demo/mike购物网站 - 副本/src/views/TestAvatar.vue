<template>
  <div class="test-container">
    <h1>测试头像点击功能</h1>
    <div class="user-profile">
      <div class="avatar" @click="handleAvatarClick">
        <img src="/images/登录前.png" alt="测试头像" class="avatar-img">
      </div>
      <div class="user-menu" :class="{ 'show': menuOpen }">
        <button @click="testAction" class="menu-item">测试菜单项</button>
      </div>
    </div>
    <button @click="toggleMenu" class="test-btn">测试菜单切换</button>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

// 菜单状态
const menuOpen = ref(false);

// 处理头像点击
const handleAvatarClick = () => {
  console.log('头像被点击！');
  menuOpen.value = !menuOpen.value;
  console.log('菜单状态切换为:', menuOpen.value);
  
  // 立即检查DOM元素状态
  const userMenu = document.querySelector('.user-menu');
  if (userMenu) {
    console.log('菜单元素当前类:', userMenu.className);
    console.log('菜单元素display:', window.getComputedStyle(userMenu).display);
    console.log('菜单元素opacity:', window.getComputedStyle(userMenu).opacity);
    console.log('菜单元素visibility:', window.getComputedStyle(userMenu).visibility);
  }
  
  // 强制更新UI
  setTimeout(() => {
    console.log('强制更新后菜单状态:', menuOpen.value);
    const userMenu = document.querySelector('.user-menu');
    if (userMenu) {
      // 直接设置类
      if (menuOpen.value) {
        userMenu.classList.add('show');
        console.log('已添加show类，当前类:', userMenu.className);
      } else {
        userMenu.classList.remove('show');
        console.log('已移除show类，当前类:', userMenu.className);
      }
      // 检查应用类后的样式
      console.log('应用类后display:', window.getComputedStyle(userMenu).display);
      console.log('应用类后opacity:', window.getComputedStyle(userMenu).opacity);
      console.log('应用类后visibility:', window.getComputedStyle(userMenu).visibility);
    } else {
      console.log('未找到user-menu元素');
    }
  }, 100);
};

// 测试菜单切换
const toggleMenu = () => {
  console.log('测试按钮被点击！');
  menuOpen.value = !menuOpen.value;
  console.log('测试按钮切换菜单状态为:', menuOpen.value);
};

// 测试菜单项点击
const testAction = () => {
  console.log('菜单项被点击！');
  menuOpen.value = false;
};
</script>

<style scoped>
.test-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
}

.user-profile {
  position: relative;
  margin: 2rem 0;
}

.avatar-img {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  cursor: pointer;
}

.user-menu {
  position: absolute;
  top: 100%;
  right: 0;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  padding: 0.75rem 0;
  width: 140px;
  z-index: 99999;
  margin-top: 0.5rem;
  display: none;
  opacity: 0;
  visibility: hidden;
  transform: translateY(0);
}

.user-menu.show {
  display: block !important;
  opacity: 1 !important;
  visibility: visible !important;
  transform: translateY(0) !important;
  transition: all 0.2s ease;
}

.menu-item {
  display: block;
  width: 100%;
  text-align: left;
  padding: 0.5rem 1rem;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 0.9rem;
}

.menu-item:hover {
  background-color: #f5f5f5;
}

.test-btn {
  padding: 0.5rem 1rem;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 1rem;
}
</style>