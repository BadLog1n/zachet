pack/age com.oneseed.zachet.data

import com.oneseed.zachet.domain.GetRatingCallback
import com.oneseed.zachet.domain.repository.Repository

class GetRatingImpl : Repository.GetRating {
    override fun getRating(getRatingCallback: GetRatingCallback) {
        //todo getRatingCallback.onRatingLoad( result)
    }
}
