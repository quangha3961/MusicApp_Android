## Giới thiệu

**MusicApp** là ứng dụng nghe nhạc Android , áp dụng kiến trúc MVP, hỗ trợ phát nhạc cả offline (local) và online (Audius API), phát nhạc nền qua Service, điều khiển qua Notification.

## Tính năng nổi bật
- **Phát nhạc offline**: Nghe các bài hát lưu trong máy (res/raw).
- **Phát nhạc online**: Lấy danh sách trending và tìm kiếm nhạc qua Audius API (miễn phí, không cần đăng nhập).
- **Mini Player**: Điều khiển phát/tạm dừng, next, prev, tua SeekBar ngay trên giao diện chính và Notification.
- **Tabbed UI**: Giao diện chia 3 tab: Local, Remote (trending), Search (tìm kiếm online).
- **Service & Notification**: Phát nhạc nền, điều khiển nhạc từ notification (play/pause/next/prev/stop).
- **MVP Architecture**: Tách biệt rõ Model - View - Presenter, dễ mở rộng và bảo trì.
- **View Binding & Data Binding**: Tránh lỗi null, code gọn gàng.

## Kiến trúc tổng quan
- **Model**: `Song`, datasource local/remote, repository.
- **View**: `MainActivity`, các Fragment (Local, Remote, Search), Adapter, layout XML.
- **Presenter**: Xử lý logic lấy dữ liệu, giao tiếp giữa View và Model.
- **Service**: `MusicService` phát nhạc nền, gửi trạng thái về Activity qua LocalBroadcastManager.
- **Notification**: `MusicNotificationManager` tạo notification điều khiển nhạc.

## Giao diện
- **TabLayout + ViewPager2**: 3 tab (Local, Remote, Search).
- **Mini Player**: Ảnh, tên bài hát, SeekBar, nút prev/play/next nổi bật.
- **Notification**: Điều khiển nhạc trực tiếp, icon hệ thống.

## Hướng dẫn sử dụng
- **Tab Local**: Nghe nhạc offline, chọn bài để phát.
- **Tab Remote**: Xem và phát nhạc trending từ Audius.
- **Tab Search**: Tìm kiếm nhạc online, phát trực tiếp.
- **Mini Player**: Điều khiển nhanh, tua, next, prev, play/pause.
- **Notification**: Điều khiển nhạc khi app chạy nền hoặc tắt màn hình.
- **Tắt nhạc**: Nhấn nút Stop trên notification.


## Cấu trúc thư mục
- `data/model` - Định nghĩa dữ liệu
- `data/datasource` - Lấy nhạc local/remote
- `data/repository` - Kết hợp nguồn nhạc
- `presenter` - Logic trung gian
- `view` - Activity, Fragment, Adapter, layout
- `service` - Phát nhạc nền
- `notification` - Notification điều khiển nhạc

