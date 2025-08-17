<template>
  <div class="customer-service-container">
    <!-- 顶部导航 -->
    <div class="cs-header">
      <el-page-header @back="goBack" content="在线客服" />
    </div>

    <!-- 客服主体内容 -->
    <div class="cs-content">
      <!-- 客服信息卡片 -->
      <div class="cs-info-card">
        <div class="cs-avatar">
          <div class="cs-avatar-placeholder">👨‍💼</div>
        </div>
        <div class="cs-details">
          <h3 class="cs-name">aaa</h3>
          <p class="cs-status">在线</p>
          <p class="cs-desc">为您提供专业的购物咨询服务</p>
        </div>
      </div>

      <!-- 快捷服务 -->
      <div class="quick-services">
        <h4 class="section-title">快捷服务</h4>
        <div class="service-grid">
          <div class="service-item" @click="startChat('order')">
            <div class="service-icon">📦</div>
            <div class="service-text">订单咨询</div>
          </div>
          <div class="service-item" @click="startChat('product')">
            <div class="service-icon">🛍️</div>
            <div class="service-text">商品咨询</div>
          </div>
          <div class="service-item" @click="startChat('payment')">
            <div class="service-icon">💳</div>
            <div class="service-text">支付问题</div>
          </div>
          <div class="service-item" @click="startChat('shipping')">
            <div class="service-icon">🚚</div>
            <div class="service-text">物流查询</div>
          </div>
          <div class="service-item" @click="startChat('return')">
            <div class="service-icon">↩️</div>
            <div class="service-text">退换货</div>
          </div>
          <div class="service-item" @click="startChat('other')">
            <div class="service-icon">❓</div>
            <div class="service-text">其他问题</div>
          </div>
        </div>
      </div>

      <!-- 常见问题 -->
      <div class="faq-section">
        <h4 class="section-title">常见问题</h4>
        <div class="faq-list">
          <div class="faq-item" @click="showFAQ('delivery')">
            <div class="faq-question">配送需要多长时间？</div>
            <div class="faq-arrow">›</div>
          </div>
          <div class="faq-item" @click="showFAQ('return')">
            <div class="faq-question">如何申请退换货？</div>
            <div class="faq-arrow">›</div>
          </div>
          <div class="faq-item" @click="showFAQ('payment')">
            <div class="faq-question">支持哪些支付方式？</div>
            <div class="faq-arrow">›</div>
          </div>
          <div class="faq-item" @click="showFAQ('discount')">
            <div class="faq-question">如何使用优惠券？</div>
            <div class="faq-arrow">›</div>
          </div>
        </div>
      </div>

      <!-- 联系方式 -->
      <div class="contact-section">
        <h4 class="section-title">其他联系方式</h4>
        <div class="contact-list">
          <div class="contact-item" @click="callService">
            <div class="contact-icon">📞</div>
            <div class="contact-info">
              <div class="contact-title">客服热线</div>
              <div class="contact-desc">400-123-4567</div>
            </div>
          </div>
          <div class="contact-item" @click="showWorkTime">
            <div class="contact-icon">🕒</div>
            <div class="contact-info">
              <div class="contact-title">工作时间</div>
              <div class="contact-desc">周一至周日 9:00-18:00</div>
            </div>
          </div>
          <div class="contact-item" @click="showEmail">
            <div class="contact-icon">📧</div>
            <div class="contact-info">
              <div class="contact-title">邮箱咨询</div>
              <div class="contact-desc">service@mikeshop.com</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="cs-footer">
      <button class="cs-btn cs-btn-primary" @click="startChat('general')">
        💬 开始聊天
      </button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';

const router = useRouter();

// 返回上一页
const goBack = () => {
  router.back();
};

// 开始聊天
const startChat = (type) => {
  router.push({ name: 'chatRoom' });
};

// 显示常见问题答案
const showFAQ = (type) => {
  const faqMap = {
    'delivery': '一般情况下，下单后1-3个工作日内发货，发货后3-7个工作日送达。具体配送时间根据您的收货地址而定。',
    'return': '在收到商品7天内，如商品完好无损，可以申请退换货。请在"我的订单"中找到相应订单，点击"申请售后"即可。',
    'payment': '我们支持支付宝、微信支付、银行卡支付等多种支付方式，确保您的支付安全便捷。',
    'discount': '在结算页面可以选择使用优惠券，也可以在个人中心的"优惠券"页面查看和管理您的优惠券。'
  };
  
  ElMessageBox.alert(
    faqMap[type],
    '常见问题',
    {
      confirmButtonText: '我知道了',
      type: 'info'
    }
  );
};

// 拨打客服电话
const callService = () => {
  ElMessageBox.confirm(
    '400-123-4567\n工作时间：9:00-18:00',
    '客服热线',
    {
      confirmButtonText: '拨打电话',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(() => {
    ElMessage.success('正在拨打电话...');
    // 在实际应用中，这里可以调用电话拨号功能
  });
};

// 显示工作时间
const showWorkTime = () => {
  ElMessageBox.alert(
    '周一至周日：9:00-18:00\n节假日正常服务，客服人员24小时轮班为您服务。',
    '工作时间',
    {
      confirmButtonText: '我知道了',
      type: 'info'
    }
  );
};

// 显示邮箱信息
const showEmail = () => {
  ElMessageBox.alert(
    'service@mikeshop.com\n我们会在24小时内回复您的邮件，请耐心等待。',
    '邮箱咨询',
    {
      confirmButtonText: '我知道了',
      type: 'info'
    }
  );
};
</script>

<style scoped>
.customer-service-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
}

.cs-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background-color: #fff;
}

.cs-content {
  flex: 1;
  padding: 16px;
  padding-bottom: 80px;
}

/* 客服信息卡片 */
.cs-info-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 24px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.cs-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 16px;
  background-color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cs-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cs-avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  background-color: #f0f0f0;
  border-radius: 50%;
}

.cs-details {
  flex: 1;
}

.cs-name {
  font-size: 20px;
  font-weight: bold;
  margin: 0 0 4px 0;
}

.cs-status {
  font-size: 14px;
  margin: 0 0 8px 0;
  opacity: 0.9;
}

.cs-desc {
  font-size: 14px;
  margin: 0;
  opacity: 0.8;
}

/* 快捷服务 */
.quick-services {
  margin-bottom: 24px;
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  margin: 0 0 16px 0;
  color: #333;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.service-item {
  background-color: white;
  padding: 16px;
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.service-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.service-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.service-text {
  font-size: 14px;
  color: #333;
}

/* 常见问题 */
.faq-section {
  margin-bottom: 24px;
}

.faq-list {
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
}

.faq-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.faq-item:last-child {
  border-bottom: none;
}

.faq-item:hover {
  background-color: #f8f9fa;
}

.faq-question {
  font-size: 14px;
  color: #333;
  flex: 1;
}

.faq-arrow {
  font-size: 18px;
  color: #999;
}

/* 联系方式 */
.contact-section {
  margin-bottom: 24px;
}

.contact-list {
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
}

.contact-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.contact-item:last-child {
  border-bottom: none;
}

.contact-item:hover {
  background-color: #f8f9fa;
}

.contact-icon {
  font-size: 20px;
  margin-right: 12px;
}

.contact-info {
  flex: 1;
}

.contact-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 2px;
}

.contact-desc {
  font-size: 12px;
  color: #666;
}

/* 底部操作栏 */
.cs-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: white;
  padding: 12px 16px;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);
  z-index: 100;
}

.cs-btn {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cs-btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.cs-btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}
</style>