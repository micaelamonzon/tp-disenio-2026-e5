
document.addEventListener('click', (e) => {
    if (!e.target.closest('#markAllReadBtn')) return;

    document.querySelectorAll('#notificationsList .notification-card.unread')
        .forEach(card => card.classList.remove('unread'));

    document.querySelectorAll('#notificationsList .notification-unread-dot')
        .forEach(dot => dot.remove());
});