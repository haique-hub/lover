<template>
  <div class="comment-thread">
    <div v-for="comment in comments" :key="comment.id" class="comment-item">
      <div class="comment-main">
        <div class="comment-header">
          <div class="comment-user">
            <img v-if="comment.userAvatar" :src="comment.userAvatar" class="avatar" />
            <div v-else class="avatar avatar-fallback">{{ comment.userNickname?.slice(0, 1) || '爱' }}</div>
            <div>
              <div class="name-row">
                <span class="name">{{ comment.userNickname }}</span>
                <span v-if="comment.replyUserNickname" class="reply-target">回复 {{ comment.replyUserNickname }}</span>
              </div>
              <span class="time">{{ formatDateTime(comment.createTime) }}</span>
            </div>
          </div>
          <div class="comment-actions">
            <el-button link type="primary" @click="$emit('reply', comment)">回复</el-button>
            <el-button
              v-if="canDelete(comment)"
              link
              type="danger"
              @click="$emit('delete', comment.id)"
            >
              删除
            </el-button>
          </div>
        </div>
        <div class="comment-content">{{ comment.content }}</div>
      </div>

      <CommentThread
        v-if="comment.children?.length"
        :comments="comment.children"
        :current-user-id="currentUserId"
        :photo-owner-id="photoOwnerId"
        @reply="$emit('reply', $event)"
        @delete="$emit('delete', $event)"
      />
    </div>
  </div>
</template>

<script setup>
defineOptions({ name: 'CommentThread' })

const props = defineProps({
  comments: {
    type: Array,
    default: () => []
  },
  currentUserId: {
    type: Number,
    default: 0
  },
  photoOwnerId: {
    type: Number,
    default: 0
  }
})

defineEmits(['reply', 'delete'])

const formatDateTime = (value) => {
  if (!value) {
    return '--'
  }
  return value.replace('T', ' ')
}

const canDelete = (comment) => comment.userId === props.currentUserId || props.photoOwnerId === props.currentUserId
</script>

<style scoped>
.comment-thread {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-top: 14px;
  padding-left: 18px;
  border-left: 2px solid rgba(210, 123, 147, 0.18);
}

.comment-item {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.comment-main {
  padding: 16px;
  border-radius: 20px;
  border: 1px solid rgba(210, 123, 147, 0.12);
  background: rgba(255, 255, 255, 0.68);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
}

.comment-user {
  display: flex;
  gap: 12px;
  align-items: center;
}

.avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-fallback {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(145deg, #ffdbe4, #fff0e8);
  color: #9f5a70;
  font-family: var(--font-serif);
  font-size: 20px;
  font-weight: 400;
}

.name-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.name {
  font-weight: 700;
  color: var(--muted-strong);
}

.reply-target,
.time {
  color: var(--muted);
  font-size: 13px;
}

.comment-content {
  margin-top: 12px;
  white-space: pre-wrap;
  line-height: 1.7;
}
</style>
