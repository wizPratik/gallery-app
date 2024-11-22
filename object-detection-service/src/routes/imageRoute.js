import express from 'express';
import { getImageDescription } from '../controllers/imageController.js';

const router = express.Router();

router.get('/:imageId', getImageDescription);

export default router;