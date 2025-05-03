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
        let recipeId = button.getAttribute('data-recipe-id');
        let currentStatus = parseInt(button.getAttribute('data-bookmark-status'));
        
        let newStatus;
        let newType;

        // Cycle through: 0 -> 1 -> 2 -> 0
        if (currentStatus === 0) {
            newStatus = 1;  // PAST
            newType = 'PAST';
        } else if (currentStatus === 1) {
            newStatus = 2;  // FUTURE
            newType = 'FUTURE';
        } else if (currentStatus === 3) {
            newStatus = 0;  // UNBOOKMARK
            newType = null; // No type needed for unbookmark
        }

        // Build the correct URL for your backend
        let url;
        if (newStatus === 0) {
            // Unbookmark request
            url = `/recipe/${recipeId}/bookmark/false`;
        } else {
            // Bookmark request
            url = `/recipe/${recipeId}/bookmark/true/${newType}`;
        }

        // Send AJAX request
        fetch(url, {
            method: 'POST',
            headers: {
                'X-Requested-With': 'XMLHttpRequest',
            }
        })
        .then(response => {
            if (response.ok) {
                // Update button classes based on new state
                button.classList.remove('past-bookmark', 'future-bookmark', 'not-bookmarked');
                button.classList.remove('fa-solid', 'fa-regular');

                if (newStatus === 1) {
                    button.classList.add('past-bookmark', 'fa-solid');
                } else if (newStatus === 2) {
                    button.classList.add('future-bookmark', 'fa-solid');
                } else if (newStatus === 0) {
                    button.classList.add('not-bookmarked', 'fa-regular');
                }

                // Update the data attribute counter
                button.setAttribute('data-bookmark-status', newStatus);
            } else {
                console.error('Error toggling bookmark');
            }
        })
        .catch(error => {
            console.error('Request failed', error);
        });
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
