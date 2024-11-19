const express = require('express');
const { getImageDescription } = require('../controllers/imageController');

const router = express.Router();

router.get('/:imageId', getImageDescription);

module.exports = router;