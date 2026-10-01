/**
 * FAJL: admin-gallery.js
 * SVRHA: Preview slika pre uploada i drag & drop ponašanje admin galerije.
 * NAPOMENA: Backend validacija i stvarno čuvanje slika su u AdminSettingsController.java.
 */

const galleryFiles = document.getElementById('galleryFiles');
const galleryPreview = document.getElementById('galleryPreview');
const galleryDropzone = document.getElementById('galleryDropzone');

// Pravi lokalni preview izabranih fajlova; još ništa nije poslato na server.
function renderGalleryPreview(files) {
    if (!galleryPreview) return;
    galleryPreview.innerHTML = '';
    const list = [...(files || [])];
    galleryPreview.hidden = list.length === 0;

    list.slice(0, 12).forEach(file => {
        const card = document.createElement('div');
        card.className = 'gallery-preview-card';

        const image = document.createElement('img');
        image.alt = file.name;
        image.src = URL.createObjectURL(file);
        image.onload = () => URL.revokeObjectURL(image.src);

        const label = document.createElement('small');
        label.textContent = file.name;

        card.append(image, label);
        galleryPreview.appendChild(card);
    });

    if (list.length > 12) {
        const more = document.createElement('div');
        more.className = 'gallery-preview-more';
        more.textContent = `+ još ${list.length - 12}`;
        galleryPreview.appendChild(more);
    }
}

if (galleryFiles) {
    galleryFiles.addEventListener('change', () => renderGalleryPreview(galleryFiles.files));
}

// Drag & drop samo puni isti <input type="file">; upload radi tek na submit forme.
if (galleryDropzone && galleryFiles) {
    ['dragenter', 'dragover'].forEach(eventName => {
        galleryDropzone.addEventListener(eventName, event => {
            event.preventDefault();
            galleryDropzone.classList.add('dragging');
        });
    });

    ['dragleave', 'drop'].forEach(eventName => {
        galleryDropzone.addEventListener(eventName, event => {
            event.preventDefault();
            galleryDropzone.classList.remove('dragging');
        });
    });

    galleryDropzone.addEventListener('drop', event => {
        const files = [...event.dataTransfer.files].filter(file => file.type.startsWith('image/'));
        if (!files.length || typeof DataTransfer === 'undefined') return;
        const transfer = new DataTransfer();
        files.forEach(file => transfer.items.add(file));
        galleryFiles.files = transfer.files;
        renderGalleryPreview(galleryFiles.files);
    });
}
