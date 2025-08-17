<template>
  <div class="jd-member-center min-h-screen bg-gray-50 flex flex-col">
    <!-- 京东风格头部 -->
    <div class="jd-header">
      <div class="jd-header-content">
        <div class="jd-back-btn" @click="goBack">
          <i class="bi bi-arrow-left"></i>
        </div>
        <h1 class="jd-page-title">会员中心</h1>
      </div>
    </div>

    <!-- 会员等级卡片 -->
    <div class="jd-member-card">
      <div class="jd-card-bg"></div>
      <div class="jd-card-content">
        <div class="jd-member-info">
          <div class="jd-avatar-section">
            <img :src="memberInfo.avatar" alt="会员头像" class="jd-avatar">
            <div class="jd-level-badge jd-level-{{ memberInfo.level }}">
              {{ memberInfo.levelName }}
            </div>
          </div>
          <div class="jd-member-details">
            <h2 class="jd-member-name">{{ memberInfo.name }}</h2>
            <p class="jd-member-id">ID: {{ memberInfo.id }}</p>
            <div class="jd-progress-section">
              <div class="jd-progress-label">
                <span>成长值</span>
                <span>{{ memberInfo.growthValue }}/{{ memberInfo.nextLevelGrowth }}</span>
              </div>
              <div class="jd-progress-bar">
                <div class="jd-progress-fill" :style="{width: memberInfo.growthProgress + '%'}"></div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="jd-benefits-row">
          <div class="jd-benefit-item">
            <div class="jd-benefit-icon">🎁</div>
            <div class="jd-benefit-info">
              <div class="jd-benefit-value">{{ memberInfo.coupons }}</div>
              <div class="jd-benefit-label">优惠券</div>
            </div>
          </div>
          <div class="jd-benefit-item">
            <div class="jd-benefit-icon">💰</div>
            <div class="jd-benefit-info">
              <div class="jd-benefit-value">{{ memberInfo.points }}</div>
              <div class="jd-benefit-label">京豆</div>
            </div>
          </div>
          <div class="jd-benefit-item">
            <div class="jd-benefit-icon">⭐</div>
            <div class="jd-benefit-info">
              <div class="jd-benefit-value">{{ memberInfo.balance }}</div>
              <div class="jd-benefit-label">余额</div>
            </div>
          </div>
          <div class="jd-benefit-item">
            <div class="jd-benefit-icon">🎫</div>
            <div class="jd-benefit-info">
              <div class="jd-benefit-value">{{ memberInfo.redPackets }}</div>
              <div class="jd-benefit-label">红包</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 会员特权 -->
    <div class="jd-privileges-section">
      <div class="jd-section-header">
        <h2 class="jd-section-title">会员特权</h2>
        <span class="jd-section-more">查看全部 <i class="bi bi-chevron-right"></i></span>
      </div>
      <div class="jd-privileges-grid">
        <div v-for="privilege in privileges" :key="privilege.id" class="jd-privilege-card">
          <div class="jd-privilege-icon">{{ privilege.icon }}</div>
          <h3 class="jd-privilege-title">{{ privilege.title }}</h3>
          <p class="jd-privilege-desc">{{ privilege.description }}</p>
        </div>
      </div>
    </div>

    <!-- 成长任务 -->
    <div class="jd-tasks-section">
      <div class="jd-section-header">
        <h2 class="jd-section-title">成长任务</h2>
        <span class="jd-section-more">更多任务 <i class="bi bi-chevron-right"></i></span>
      </div>
      <div class="jd-tasks-list">
        <div v-for="task in growthTasks" :key="task.id" class="jd-task-item">
          <div class="jd-task-info">
            <div class="jd-task-icon">{{ task.icon }}</div>
            <div class="jd-task-details">
              <h4 class="jd-task-title">{{ task.title }}</h4>
              <p class="jd-task-desc">{{ task.description }}</p>
            </div>
          </div>
          <div class="jd-task-reward">
            <span class="jd-reward-value">+{{ task.reward }}</span>
            <span class="jd-reward-label">成长值</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 专属服务 -->
    <div class="jd-services-section">
      <div class="jd-section-header">
        <h2 class="jd-section-title">专属服务</h2>
      </div>
      <div class="jd-services-grid">
        <div v-for="service in exclusiveServices" :key="service.id" class="jd-service-item">
          <div class="jd-service-icon">{{ service.icon }}</div>
          <h4 class="jd-service-title">{{ service.title }}</h4>
          <p class="jd-service-desc">{{ service.description }}</p>
          <button class="jd-service-btn" @click="useService(service.id)">{{ service.buttonText }}</button>
        </div>
      </div>
    </div>

    <!-- 升级会员弹窗 -->
    <div v-if="showUpgradeModal" class="jd-modal">
      <div class="jd-modal-content">
        <div class="jd-modal-header">
          <h3 class="jd-modal-title">升级会员</h3>
          <div class="jd-modal-close" @click="showUpgradeModal = false">
            <i class="bi bi-x"></i>
          </div>
        </div>
        <div class="jd-modal-body">
          <div class="jd-upgrade-options">
            <div v-for="option in upgradeOptions" :key="option.id" class="jd-upgrade-option" 
                 :class="{'jd-selected': selectedOption === option.id}" @click="selectedOption = option.id">
              <div class="jd-option-header">
                <h4 class="jd-option-title">{{ option.title }}</h4>
                <span class="jd-option-price">¥{{ option.price }}</span>
              </div>
              <div class="jd-option-features">
                <div v-for="feature in option.features" :key="feature" class="jd-option-feature">
                  <i class="bi bi-check"></i> {{ feature }}
                </div>
              </div>
            </div>
          </div>
          <div class="jd-modal-actions">
            <button class="jd-cancel-btn" @click="showUpgradeModal = false">取消</button>
            <button class="jd-confirm-btn" @click="handleUpgrade">立即升级</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';

// 京东风格会员信息类型定义
interface JDMemberInfo {
  id: string;
  name: string;
  avatar: string;
  level: number;
  levelName: string;
  growthValue: number;
  nextLevelGrowth: number;
  growthProgress: number;
  points: number;
  coupons: number;
  balance: number;
  redPackets: number;
}

// 特权类型定义
interface Privilege {
  id: number;
  icon: string;
  title: string;
  description: string;
}

// 成长任务类型定义
interface GrowthTask {
  id: number;
  icon: string;
  title: string;
  description: string;
  reward: number;
  completed: boolean;
}

// 专属服务类型定义
interface ExclusiveService {
  id: number;
  icon: string;
  title: string;
  description: string;
  buttonText: string;
}

// 升级选项类型定义
interface UpgradeOption {
  id: number;
  title: string;
  price: number;
  features: string[];
}

// 响应式变量
const router = useRouter();
const showUpgradeModal = ref(false);
const selectedOption = ref(1);

// 京东风格会员数据
const memberInfo: JDMemberInfo = {
  id: 'MIKE2023001',
  name: 'Mike Johnson',
  avatar: '/images/登录前.png',
  level: 3,
  levelName: '金牌会员',
  growthValue: 2580,
  nextLevelGrowth: 5000,
  growthProgress: 51.6,
  points: 1250,
  coupons: 8,
  balance: 168.50,
  redPackets: 3
};

// 京东风格特权数据
const privileges: Privilege[] = [
  { id: 1, icon: '🎁', title: '生日礼遇', description: '生日当月可领取专属礼品' },
  { id: 2, icon: '💸', title: '购物折扣', description: '享受全场9.5折优惠' },
  { id: 3, icon: '🚚', title: '免费配送', description: '订单满49元免运费' },
  { id: 4, icon: '🎫', title: '专属活动', description: '优先参与会员专属活动' },
  { id: 5, icon: '🔄', title: '免费退换', description: '享受7天无理由退换货' },
  { id: 6, icon: '🎮', title: '游戏特权', description: '会员专享游戏礼包' }
];

// 成长任务数据
const growthTasks: GrowthTask[] = [
  { id: 1, icon: '📝', title: '完善资料', description: '完善个人资料信息', reward: 50, completed: true },
  { id: 2, icon: '🛒', title: '首单购物', description: '完成首次购物订单', reward: 100, completed: true },
  { id: 3, icon: '⭐', title: '评价晒单', description: '评价已购商品', reward: 30, completed: false },
  { id: 4, icon: '👥', title: '邀请好友', description: '邀请新用户注册', reward: 200, completed: false },
  { id: 5, icon: '📱', title: 'APP签到', description: '连续7天APP签到', reward: 40, completed: false }
];

// 专属服务数据
const exclusiveServices: ExclusiveService[] = [
  { id: 1, icon: '💬', title: '专属客服', description: '7×24小时专属客服服务', buttonText: '立即咨询' },
  { id: 2, icon: '🚀', title: '极速退款', description: '享受极速退款服务', buttonText: '开通服务' },
  { id: 3, icon: '🎯', title: '专属推荐', description: '个性化商品推荐', buttonText: '查看推荐' },
  { id: 4, icon: '🎊', title: '会员活动', description: '参与会员专属活动', buttonText: '参与活动' }
];

// 升级选项数据
const upgradeOptions: UpgradeOption[] = [
  { id: 1, title: '钻石会员', price: 998, features: ['所有金牌会员权益', '专属客服经理', '生日专属礼品', '免费退换货延长至15天'] },
  { id: 2, title: '超级会员', price: 1998, features: ['所有钻石会员权益', '专属购物顾问', 'VIP专属通道', '年度体检服务'] }
];

// 方法定义
const goBack = () => {
  router.push('/mikeshop');
};

const showUpgradeForm = () => {
  showUpgradeModal.value = true;
  selectedOption.value = 1;
};

const handleUpgrade = () => {
  const option = upgradeOptions.find(opt => opt.id === selectedOption.value);
  if (option) {
    alert(`成功升级为${option.title}！`);
    showUpgradeModal.value = false;
  }
};

const useService = (serviceId: number) => {
  const service = exclusiveServices.find(s => s.id === serviceId);
  if (service) {
    alert(`${service.title}功能正在开发中...`);
  }
};
</script>

<style scoped>
/* 京东风格基础样式 */
.jd-member-center {
  width: 100%;
  margin: 0;
  padding: 0;
}

.jd-member-center {
  font-family: -apple-system, blinkmacsystemfont, 'segoe ui', 'pingfang sc', 'hiragino sans gb', 'microsoft yahei', 'helvetica neue', helvetica, arial, sans-serif;
  background: #f5f5f5;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 京东风格头部 */
.jd-header {
 background: linear-gradient(135deg, #e1251b 0%, #c81623 100%);
 color: white;
 padding: 12px ;
 position: sticky;
 top: 0;
 z-index: 100;
}

.jd-header-content {
  display: flex;
  align-items: center;
  width: 100%;
  margin: 0;
  padding: 0 16px;
}

.jd-back-btn {
  font-size: 20px;
  cursor: pointer;
  margin-right: 16px;
}

.jd-page-title {
  font-size: 18px;
  font-weight: 500;
  margin: 0;
}

/* 京东风格会员卡片 */
.jd-member-card {
  margin: -20px 0 32px;
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}

.jd-card-bg {
  background: linear-gradient(135deg, #e1251b 0%, #c81623 50%, #8b0000 100%);
  height: 120px;
  position: relative;
}

.jd-card-bg::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="20" cy="20" r="2" fill="rgba(255,255,255,0.1)"/><circle cx="80" cy="80" r="3" fill="rgba(255,255,255,0.1)"/><circle cx="50" cy="10" r="1" fill="rgba(255,255,255,0.1)"/></svg>');
  opacity: 0.3;
}

.jd-card-content {
  background: white;
  padding: 25px;
  margin-top: -60px;
  position: relative;
  z-index: 2;
}

.jd-member-info {
  display: flex;
  align-items: center;
  margin-bottom: 25px;
}

.jd-avatar-section {
  position: relative;
  margin-right: 16px;
}

.jd-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 3px solid white;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  object-fit: cover;
}

.jd-level-badge {
  position: absolute;
  bottom: -5px;
  right: -5px;
  background: linear-gradient(135deg, #ffd700 0%, #ffb347 100%);
  color: #8b4513;
  font-size: 10px;
  font-weight: bold;
  padding: 4px 8px;
  border-radius: 12px;
  border: 2px solid white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.jd-level-3 {
  background: linear-gradient(135deg, #ffd700 0%, #ffb347 100%);
  color: #8b4513;
}

.jd-level-4 {
  background: linear-gradient(135deg, #c0c0c0 0%, #a8a8a8 100%);
  color: #333;
}

.jd-level-5 {
  background: linear-gradient(135deg, #b9f2ff 0%, #87ceeb 100%);
  color: #0066cc;
}

.jd-member-details {
  flex: 1;
}

.jd-member-name {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0 0 4px 0;
}

.jd-member-id {
  font-size: 12px;
  color: #999;
  margin: 0 0 12px 0;
}

.jd-progress-section {
  margin-bottom: 8px;
}

.jd-progress-label {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.jd-progress-bar {
  height: 6px;
  background: #f0f0f0;
  border-radius: 3px;
  overflow: hidden;
}

.jd-progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #e1251b 0%, #ff6b6b 100%);
  border-radius: 3px;
  transition: width 0.3s ease;
}

.jd-benefits-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

.jd-benefit-item {
  text-align: center;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.jd-benefit-item:hover {
  transform: translateY(-2px);
}

.jd-benefit-icon {
  font-size: 24px;
  margin-bottom: 4px;
}

.jd-benefit-value {
  font-size: 16px;
  font-weight: bold;
  color: #e1251b;
  margin-bottom: 2px;
}

.jd-benefit-label {
  font-size: 12px;
  color: #666;
}

/* 京东风格区块通用样式 */
.jd-privileges-section,
.jd-tasks-section,
.jd-services-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 32px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.jd-member-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.jd-section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.jd-section-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.jd-section-more {
  font-size: 12px;
  color: #999;
  cursor: pointer;
  transition: color 0.3s ease;
}

.jd-section-more:hover {
  color: #e1251b;
}

/* 京东风格特权网格 */
.jd-privileges-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 12px;
}

.jd-privilege-card {
  text-align: center;
  padding: 16px 12px;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
  cursor: pointer;
}

.jd-privilege-card:hover {
  border-color: #e1251b;
  box-shadow: 0 4px 12px rgba(225, 37, 27, 0.1);
  transform: translateY(-2px);
}

.jd-privilege-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.jd-privilege-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin: 0 0 4px 0;
}

.jd-privilege-desc {
  font-size: 11px;
  color: #999;
  line-height: 1.4;
  margin: 0;
}

/* 京东风格成长任务 */
.jd-tasks-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.jd-task-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.jd-task-item:hover {
  border-color: #e1251b;
  box-shadow: 0 2px 8px rgba(225, 37, 27, 0.1);
}

.jd-task-info {
  display: flex;
  align-items: center;
  flex: 1;
}

.jd-task-icon {
  font-size: 20px;
  margin-right: 12px;
}

.jd-task-details {
  flex: 1;
}

.jd-task-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin: 0 0 2px 0;
}

.jd-task-desc {
  font-size: 12px;
  color: #999;
  margin: 0;
}

.jd-task-reward {
  text-align: center;
  padding-left: 16px;
  border-left: 1px solid #f0f0f0;
}

.jd-reward-value {
  font-size: 16px;
  font-weight: bold;
  color: #e1251b;
  display: block;
}

.jd-reward-label {
  font-size: 11px;
  color: #999;
}

/* 京东风格专属服务 */
.jd-services-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.jd-service-item {
  text-align: center;
  padding: 20px 16px;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.jd-service-item:hover {
  border-color: #e1251b;
  box-shadow: 0 4px 12px rgba(225, 37, 27, 0.1);
  transform: translateY(-2px);
}

.jd-service-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.jd-service-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin: 0 0 6px 0;
}

.jd-service-desc {
  font-size: 12px;
  color: #999;
  line-height: 1.4;
  margin: 0 0 12px 0;
}

.jd-service-btn {
  background: linear-gradient(135deg, #e1251b 0%, #c81623 100%);
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 16px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
}

.jd-service-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(225, 37, 27, 0.3);
}

/* 京东风格弹窗 */
.jd-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
}

.jd-modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 8px 32px rgba(0,0,0,0.2);
}

.jd-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.jd-modal-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.jd-modal-close {
  font-size: 20px;
  cursor: pointer;
  color: #999;
  transition: color 0.3s ease;
  padding: 4px;
}

.jd-modal-close:hover {
  color: #333;
}

.jd-modal-body {
  padding: 20px;
}

.jd-upgrade-options {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 24px;
}

.jd-upgrade-option {
  border: 2px solid #f0f0f0;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.jd-upgrade-option:hover {
  border-color: #e1251b;
  box-shadow: 0 4px 12px rgba(225, 37, 27, 0.1);
}

.jd-upgrade-option.jd-selected {
  border-color: #e1251b;
  background: linear-gradient(135deg, rgba(225, 37, 27, 0.05) 0%, rgba(200, 22, 35, 0.05) 100%);
}

.jd-option-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.jd-option-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.jd-option-price {
  font-size: 18px;
  font-weight: bold;
  color: #e1251b;
}

.jd-option-features {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.jd-option-feature {
  font-size: 12px;
  color: #666;
  display: flex;
  align-items: center;
}

.jd-option-feature i {
  color: #4caf50;
  margin-right: 6px;
  font-size: 14px;
}

.jd-modal-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.jd-cancel-btn {
  background: transparent;
  color: #666;
  border: 1px solid #ddd;
  padding: 10px 20px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.jd-cancel-btn:hover {
  border-color: #999;
  color: #333;
}

.jd-confirm-btn {
  background: linear-gradient(135deg, #e1251b 0%, #c81623 100%);
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.jd-confirm-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(225, 37, 27, 0.3);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .jd-member-center {
    padding: 0 12px;
  }

  .jd-header-content {
    padding: 0 12px;
    max-width: 100%;
  }

  .jd-page-title {
    font-size: 16px;
  }

  .jd-back-btn {
    margin-right: 12px;
    padding: 8px;
  }

  .jd-member-card {
    margin: -20px 12px 24px;
  }

  .jd-card-content {
    padding: 20px;
    margin-top: -50px;
  }

  .jd-member-info {
    flex-direction: column;
    text-align: center;
    margin-bottom: 20px;
  }

  .jd-avatar-section {
    margin-right: 0;
    margin-bottom: 16px;
  }

  .jd-avatar {
    width: 70px;
    height: 70px;
  }

  .jd-member-name {
    font-size: 16px;
  }

  .jd-benefits-row {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
    padding-top: 12px;
  }

  .jd-benefit-icon {
    font-size: 20px;
  }

  .jd-benefit-value {
    font-size: 14px;
  }

  .jd-privileges-grid {
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  }

  .jd-services-grid {
    grid-template-columns: 1fr;
  }

  .jd-task-item {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }

  .jd-task-reward {
    border-left: none;
    border-top: 1px solid #f0f0f0;
    padding-left: 0;
    padding-top: 12px;
  }
}
</style>