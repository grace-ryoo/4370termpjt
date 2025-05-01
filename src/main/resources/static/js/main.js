document.addEventListener('DOMContentLoaded', function () {
    // submittable is expected to be text fields.
    var submittables = document.getElementsByClassName('submittable');

    for (var submittable of submittables) {
        // Make text field submit the enclosing form when the enter key is pressed.
        submittable.addEventListener('keydown', function (e) {
            // Check if Enter was pressed without the Shift key
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault(); // Prevent new lines.
                this.form.submit(); // Submit the form.
                console.log(this.form + ' was submitted.');
            }
        });
    }
});


document.querySelectorAll('.bookmark-btn').forEach(button => {
    button.addEventListener('click', () => {
        const currentStatus = parseInt(button.getAttribute('data-bookmark-status'));
        
        // Cycle through bookmark options
        if (currentStatus === 1) {
            button.textContent = "&#9733; Bookmarked (Option 1)";
            button.setAttribute('data-bookmark-status', '2');
        } else if (currentStatus === 2) {
            button.textContent = "&#9734; Unbookmarked";
            button.setAttribute('data-bookmark-status', '3');
        } else {
            button.textContent = "&#9734; Bookmark";
            button.setAttribute('data-bookmark-status', '1');
        }
    });
});

// Rating functionality (basic)
document.querySelectorAll('.rating-stars button').forEach(star => {
    star.addEventListener('click', () => {
        const rating = parseInt(star.getAttribute('data-rating'));
        alert('Rated ' + rating + ' stars!');
        
        // Set all stars up to the clicked one to filled (change color or add filled class)
        const allStars = star.parentElement.querySelectorAll('button');
        allStars.forEach((s, index) => {
            s.style.color = index < rating ? '#f39c12' : '#ccc'; // Yellow for filled stars
        });
    });
});
