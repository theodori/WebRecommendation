#!/bin/bash



## PARAMETERS ##
preprocess="01_preprocess"
mkdir -p $preprocess
database="02_DATABASE_5"
mkdir -p $database
validation="03_VALIDATION_5"
mkdir -p $validation



echo "## PREPROCESS ##"

../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco2.jar ehupatras.webrecommendation.A000MainClassPreprocess \
  $preprocess /LOGs_from_Jan9_toNov19.log
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco2.jar ehupatras.webrecommendation.A001MainClassCreateDatabase \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database




echo "## DISTANCE MATRIX ##"

# Raw sequences
mkdir -p $database/DM_00_no_role_dist
mkdir -p $database/DM_03_intelligent2_dist
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco2.jar ehupatras.webrecommendation.A011MainClassDistanceMatrixInverse \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database
mkdir -p 02_DATABASE_5/DM_04_edit
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco2.jar ehupatras.webrecommendation.A012MainClassDistanceMatrixED \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database

# Split sequences by index
mkdir -p $database/DM00-no_role-split
mkdir -p $database/DM03-U_HC2-split
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco2.jar ehupatras.webrecommendation.A013MainClassDistanceMatrixInverseSplit \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database
mkdir -p $database/DM04-edit-split
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco2.jar ehupatras.webrecommendation.A014MainClassDistanceMatrixEDSplit \
  01_preprocess /LOGs_from_Jan9_toNov19.log \
  $database

# Introducing topics
# URL to URL distance by euclidean distance between topic-histograms (continue value)
# it expects the file > preprocessingWD + "/URLs_DM.txt"
mkdir -p $database/DM_00_no_role_dist_topics
mkdir -p $database/DM_03_intelligent2_dist_topics
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco2.jar ehupatras.webrecommendation.A111MainClassDistanceMatrixInverseTopics \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database
mkdir -p $database/DM_04_edit_dist_topics
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco2.jar ehupatras.webrecommendation.A112MainClassDistanceMatrixEDTopics \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database
# Two URLs are the same (0); are from the same topic (0.5); or are different (1) (discrete values)
# it expects the file > preprocessingWD + "/URLs_to_topic.txt"
mkdir -p $database/DM_00_no_role_dist_topics2
mkdir -p $database/DM_03_intelligent2_dist_topics2
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco2.jar ehupatras.webrecommendation.A113MainClassDistanceMatrixInverseTopics2 \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database
mkdir -p $database/DM_04_edit_dist_topics2
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco2.jar ehupatras.webrecommendation.A114MainClassDistanceMatrixEDTopics2 \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database



echo "## EVALUATION BASED ON DATABASE: HOLD-OUT ##"

mkdir -p 03_VALIDATION
../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco2.jar ehupatras.webrecommendation.A020MainClassHoldOut \
  $preprocess /LOGs_from_Jan9_toNov19.log \
  $database "distance_matrix" \
  $validation



echo "## MARKOV CHAIN ##"

../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco2.jar ehupatras.webrecommendation.A030MainClassMarkovChain \
  01_preprocess /LOGs_from_Jan9_toNov19.log \
  $database "distance_matrix" \
  $validation



echo "## GLOBAL SUFFIX TREE ##"

../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco2.jar ehupatras.webrecommendation.A031MainClassSuffixTree \
  01_preprocess /LOGs_from_Jan9_toNov19.log \
  $database /DM_00_no_role_dist \
  $validation



echo "## Hclust ##"

for dm in "DM_00_no_role_dist" "DM_03_intelligent2_dist" "DM_04_edit" "DM00-no_role-split" "DM03-U_HC2-split" "DM04-edit-split" "DM_00_no_role_dist_topics" "DM_03_intelligent2_dist_topics" "DM_04_edit_dist_topics" "DM_00_no_role_dist_topics2" "DM_03_intelligent2_dist_topics2" "DM_04_edit_dist_topics2"
do
  hclust="/hclust_"$dm
  echo " "$hclust
  mkdir -p $validation""$hclust
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco2.jar ehupatras.webrecommendation.A040MainClassHclust \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $hclust
done



echo "## PAM ##"

for dm in "DM_00_no_role_dist" "DM_03_intelligent2_dist" "DM_04_edit" "DM00-no_role-split" "DM03-U_HC2-split" "DM04-edit-split" "DM_00_no_role_dist_topics" "DM_03_intelligent2_dist_topics" "DM_04_edit_dist_topics" "DM_00_no_role_dist_topics2" "DM_03_intelligent2_dist_topics2" "DM_04_edit_dist_topics2"
do
  pam="/pam_"$dm
  echo " "$pam
  mkdir -p $validation""$pam
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco2.jar ehupatras.webrecommendation.A041MainClassPAM \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $pam
done



echo "## HCLUST+MSA+WSEQ+ST ##"

for dm in "DM_00_no_role_dist" "DM_03_intelligent2_dist" "DM_04_edit" "DM00-no_role-split" "DM03-U_HC2-split" "DM04-edit-split" "DM_00_no_role_dist_topics" "DM_03_intelligent2_dist_topics" "DM_04_edit_dist_topics" "DM_00_no_role_dist_topics2" "DM_03_intelligent2_dist_topics2" "DM_04_edit_dist_topics2"
do
  hclust="/hclust_"$dm
  cmsast=$hclust"/msa"
  echo " "$cmsast
  mkdir -p $validation""$cmsast
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco2.jar ehupatras.webrecommendation.A050MainClassHclustMsaSt \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $hclust $cmsast
done



echo "## PAM+MSA+WSEQ+ST ##"

for dm in "DM_00_no_role_dist" "DM_03_intelligent2_dist" "DM_04_edit" "DM00-no_role-split" "DM03-U_HC2-split" "DM04-edit-split" "DM_00_no_role_dist_topics" "DM_03_intelligent2_dist_topics" "DM_04_edit_dist_topics" "DM_00_no_role_dist_topics2" "DM_03_intelligent2_dist_topics2" "DM_04_edit_dist_topics2"
do
  pam="/pam_"$dm
  cmsast=$pam"/msa"
  echo " "$cmsast
  mkdir -p $validation""$cmsast
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco2.jar ehupatras.webrecommendation.A051MainClassPAMMsaSt \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $pam $cmsast
done



echo "## HCLUST+SPADE ##"

for dm in "DM_00_no_role_dist" "DM_03_intelligent2_dist" "DM_04_edit" "DM00-no_role-split" "DM03-U_HC2-split" "DM04-edit-split" "DM_00_no_role_dist_topics" "DM_03_intelligent2_dist_topics" "DM_04_edit_dist_topics" "DM_00_no_role_dist_topics2" "DM_03_intelligent2_dist_topics2" "DM_04_edit_dist_topics2"
do
  hclust="/hclust_"$dm
  echo " "$hclust"_spade"
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco2.jar ehupatras.webrecommendation.A052MainClassHclustSpade \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $hclust
done



echo "## PAM+SPADE ##"

for dm in "DM_00_no_role_dist" "DM_03_intelligent2_dist" "DM_04_edit" "DM00-no_role-split" "DM03-U_HC2-split" "DM04-edit-split" "DM_00_no_role_dist_topics" "DM_03_intelligent2_dist_topics" "DM_04_edit_dist_topics" "DM_00_no_role_dist_topics2" "DM_03_intelligent2_dist_topics2" "DM_04_edit_dist_topics2"
do
  pam="/pam_"$dm
  echo " "$pam"_spade"
  ../jre1.7.0_51/bin/java -Xmx2048m -cp ehupatraWebReco2.jar ehupatras.webrecommendation.A053MainClassPamSpade \
    $preprocess /LOGs_from_Jan9_toNov19.log \
    $database "/"$dm \
    $validation $pam
done
