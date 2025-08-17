import express from 'express';
import cors from 'cors';
import multer from 'multer';
import path from 'path';
import fs from 'fs';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// 创建Express应用
const app = express();

// 配置CORS
app.use(cors({
  origin: ['http://localhost:5173', 'http://localhost:5174'], // 允许前端开发服务器访问
  credentials: true
}));

// 配置静态文件服务，允许访问uploads目录下的图片
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

// 确保uploads目录存在
const uploadDir = path.join(__dirname, 'uploads');
if (!fs.existsSync(uploadDir)) {
  fs.mkdirSync(uploadDir, { recursive: true });
}

// 配置multer存储
const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    cb(null, uploadDir);
  },
  filename: (req, file, cb) => {
    // 生成唯一文件名: 时间戳+原始文件名
    const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1e9);
    const filename = uniqueSuffix + path.extname(file.originalname);
    cb(null, filename);
  }
});

// 文件过滤 - 只允许图片类型
const fileFilter = (req, file, cb) => {
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif'];
  if (allowedTypes.includes(file.mimetype)) {
    cb(null, true);
  } else {
    cb(new Error('只允许上传JPG、PNG和GIF格式的图片'), false);
  }
};

// 配置multer
const upload = multer({
  storage: storage,
  fileFilter: fileFilter,
  limits: {
    fileSize: 5 * 1024 * 1024 // 限制文件大小为5MB
  }
});

// 上传接口
app.post('/api/upload', upload.single('file'), (req, res) => {
  try {
    if (!req.file) {
      return res.status(400).json({
        code: 400,
        message: '未找到上传文件',
        success: false
      });
    }

    // 返回上传成功信息和文件路径
    res.json({
      code: 200,
      message: '文件上传成功',
      success: true,
      data: {
        filePath: `/uploads/${req.file.filename}`,
        fileName: req.file.filename
      }
    });
  } catch (error) {
    res.status(500).json({
      code: 500,
      message: '文件上传失败: ' + error.message,
      success: false
    });
  }
});

// 启动服务器
const PORT = 3001;
app.listen(PORT, () => {
  console.log(`文件上传服务器已启动，监听端口 ${PORT}`);
  console.log(`上传接口: http://localhost:${PORT}/api/upload`);
});

// 错误处理中间件
app.use((err, req, res, next) => {
  console.error('服务器错误:', err);
  res.status(500).json({
    code: 500,
    message: '服务器内部错误',
    success: false
  });
});